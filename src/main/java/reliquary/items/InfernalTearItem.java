package reliquary.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.LanguageHelper;
import reliquary.util.NBTHelper;
import reliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InfernalTearItem extends ToggleableItem {
	private static final String ENABLED_TAG = "enabled";
	private static final int COOLDOWN = 4;
	private static final int NOTHING_FOUND_COOLDOWN = COOLDOWN * 5;

	public InfernalTearItem() {
		super(new Properties().stacksTo(1).setNoRepair());
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide || world.getGameTime() % COOLDOWN != 0 || !isEnabled(stack) || isInCooldown(stack, world) || !(entity instanceof Player player)) {
			return;
		}

		ItemStack tearStack = getStackFromTear(stack);
		if (tearStack.isEmpty()) {
			resetTear(stack);
			return;
		}

		Optional<Integer> experience = Settings.COMMON.items.infernalTear.getItemExperience(RegistryHelper.getItemRegistryName(tearStack.getItem()));
		if (experience.isEmpty()) {
			resetTear(stack);
			return;
		}

		int countConsumed = InventoryHelper.consumeItemStack(ist -> ItemHandlerHelper.canItemStacksStack(tearStack, ist), player, 4).getCount();
		if (countConsumed > 0) {
			player.giveExperiencePoints(experience.get() * countConsumed);
		} else {
			setCooldown(stack, world, NOTHING_FOUND_COOLDOWN);
		}
	}

	private void resetTear(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			tag.remove("item");
			tag.remove(ENABLED_TAG);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, world, tooltip, flag);
		if (getStackFromTear(stack).isEmpty()) {
			LanguageHelper.formatTooltip("tooltip.reliquary.tear_empty", null, tooltip);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable Level world, List<Component> tooltip) {
		ItemStack contents = getStackFromTear(stack);
		String itemName = contents.getHoverName().getString();

		LanguageHelper.formatTooltip("tooltip.reliquary.tear", Map.of("item", itemName), tooltip);

		if (isEnabled(stack)) {
			LanguageHelper.formatTooltip("tooltip.reliquary.absorb_active", Map.of("item", ChatFormatting.YELLOW + itemName), tooltip);
		}
		tooltip.add(Component.literal(LanguageHelper.getLocalization("tooltip." + Reference.MOD_ID + ".absorb")));
		tooltip.add(Component.literal(LanguageHelper.getLocalization("tooltip." + Reference.MOD_ID + ".infernal_tear.absorb_unset")));
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return !getStackFromTear(stack).isEmpty();
	}

	public static ItemStack getStackFromTear(ItemStack tear) {
		CompoundTag itemNBT = NBTHelper.getTagCompound("item", tear);
		if (itemNBT.isEmpty()) {
			return ItemStack.EMPTY;
		}

		return ItemStack.of(itemNBT);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		InteractionResultHolder<ItemStack> actionResult = super.use(world, player, hand);
		if (player.isShiftKeyDown() && !isEnabled(stack)) {
			return actionResult;
		}

		ItemStack itemStack = actionResult.getObject();

		//empty the tear if player is not sneaking and the tear is not empty
		CompoundTag nbt = itemStack.getTag();
		if (!player.isShiftKeyDown() && !getStackFromTear(itemStack).isEmpty()) {
			NBTHelper.remove(nbt, "item");
			NBTHelper.remove(nbt, ENABLED_TAG);

			return actionResult;
		}

		//nothing more to do with a filled tear here
		if (!getStackFromTear(itemStack).isEmpty()) {
			return actionResult;
		}

		//if user is sneaking or just enabled the tear, let's fill it
		if (player.isShiftKeyDown() || !isEnabled(itemStack)) {
			ItemStack returnStack = InventoryHelper.getItemHandlerFrom(player).map(handler -> buildTear(itemStack, handler)).orElse(ItemStack.EMPTY);
			if (!returnStack.isEmpty()) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, returnStack);
			}
		}

		//by this time the tear is still empty and there wasn't anything to put in it
		// so let's disable it if it got enabled
		if (isEnabled(itemStack)) {
			toggleEnabled(itemStack);
		}
		return actionResult;
	}

	private ItemStack buildTear(ItemStack stack, IItemHandler inventory) {
		ItemStack tear = new ItemStack(this, 1);

		ItemStack target = getTargetAlkahestItem(stack, inventory);
		if (target.isEmpty()) {
			return ItemStack.EMPTY;
		}

		setTearTarget(tear, target);

		if (Boolean.TRUE.equals(Settings.COMMON.items.infernalTear.absorbWhenCreated.get())) {
			NBTHelper.putBoolean(ENABLED_TAG, stack, true);
		}

		return tear;
	}

	public static void setTearTarget(ItemStack tear, ItemStack target) {
		NBTHelper.putTagCompound("item", tear, target.save(new CompoundTag()));
	}

	private ItemStack getTargetAlkahestItem(ItemStack self, IItemHandler inventory) {
		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty() || self.sameItem(stack) || stack.getMaxStackSize() == 1 || stack.getTag() != null
					|| Settings.COMMON.items.infernalTear.getItemExperience(RegistryHelper.getItemRegistryName(stack.getItem())).isEmpty()) {
				continue;
			}
			if (InventoryHelper.getItemQuantity(stack, inventory) > itemQuantity) {
				itemQuantity = InventoryHelper.getItemQuantity(stack, inventory);
				targetItem = stack.copy();
			}
		}
		return targetItem;
	}
}
