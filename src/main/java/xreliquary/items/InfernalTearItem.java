package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class InfernalTearItem extends ToggleableItem {
	private static final String ENABLED_TAG = "enabled";

	public InfernalTearItem() {
		super(new Properties().maxStackSize(1).setNoRepair());
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isRemote || !isEnabled(stack) || !(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;

		if (getStackFromTear(stack).isEmpty()) {
			resetTear(stack);
			return;
		}

		Optional<Integer> experience = Settings.COMMON.items.infernalTear.getItemExperience(RegistryHelper.getItemRegistryName(getStackFromTear(stack).getItem()));
		if (!experience.isPresent()) {
			resetTear(stack);
			return;
		}

		if (InventoryHelper.consumeItem(getStackFromTear(stack), player)) {
			player.giveExperiencePoints(experience.get());
		}
	}

	private void resetTear(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag != null) {
			tag.remove("item");
			tag.remove(ENABLED_TAG);
		}
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		if (getStackFromTear(stack).isEmpty()) {
			LanguageHelper.formatTooltip("tooltip.xreliquary.tear_empty", null, tooltip);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void addMoreInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip) {
		ItemStack contents = getStackFromTear(stack);
		String itemName = contents.getDisplayName().getString();

		LanguageHelper.formatTooltip("tooltip.xreliquary.tear", ImmutableMap.of("item", itemName), tooltip);

		if (isEnabled(stack)) {
			LanguageHelper.formatTooltip("tooltip.xreliquary.absorb_active", ImmutableMap.of("item", TextFormatting.YELLOW + itemName), tooltip);
		}
		tooltip.add(new StringTextComponent(LanguageHelper.getLocalization("tooltip." + Reference.MOD_ID + ".absorb")));
		tooltip.add(new StringTextComponent(LanguageHelper.getLocalization("tooltip." + Reference.MOD_ID + ".infernal_tear.absorb_unset")));
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return !getStackFromTear(stack).isEmpty();
	}

	public static ItemStack getStackFromTear(ItemStack tear) {
		CompoundNBT itemNBT = NBTHelper.getTagCompound("item", tear);
		if (itemNBT.isEmpty()) {
			return ItemStack.EMPTY;
		}

		return ItemStack.read(itemNBT);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.EPIC;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		ActionResult<ItemStack> actionResult = super.onItemRightClick(world, player, hand);
		if (player.isSneaking() && !isEnabled(stack)) {
			return actionResult;
		}

		ItemStack itemStack = actionResult.getResult();

		//empty the tear if player is not sneaking and the tear is not empty
		CompoundNBT nbt = itemStack.getTag();
		if (!player.isSneaking() && !getStackFromTear(itemStack).isEmpty()) {
			NBTHelper.remove(nbt, "item");
			NBTHelper.remove(nbt, ENABLED_TAG);

			return actionResult;
		}

		//nothing more to do with a filled tear here
		if (!getStackFromTear(itemStack).isEmpty()) {
			return actionResult;
		}

		//if user is sneaking or just enabled the tear, let's fill it
		if (player.isSneaking() || !isEnabled(itemStack)) {
			ItemStack returnStack = InventoryHelper.getItemHandlerFrom(player).map(handler -> buildTear(itemStack, handler)).orElse(ItemStack.EMPTY);
			if (!returnStack.isEmpty()) {
				return new ActionResult<>(ActionResultType.SUCCESS, returnStack);
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
		NBTHelper.putTagCompound("item", tear, target.write(new CompoundNBT()));
	}

	private ItemStack getTargetAlkahestItem(ItemStack self, IItemHandler inventory) {
		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty() || self.isItemEqual(stack) || stack.getMaxStackSize() == 1 || stack.getTag() != null
					|| !Settings.COMMON.items.infernalTear.getItemExperience(RegistryHelper.getItemRegistryName(stack.getItem())).isPresent()) {
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
