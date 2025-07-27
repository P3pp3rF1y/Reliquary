package reliquary.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import reliquary.Reliquary;
import reliquary.init.ModDataComponents;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.RegistryHelper;
import reliquary.util.TooltipBuilder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class InfernalTearItem extends ToggleableItem {
	private static final int COOLDOWN = 4;
	private static final int NOTHING_FOUND_COOLDOWN = COOLDOWN * 5;

	public InfernalTearItem() {
		super(new Properties().stacksTo(1).setNoRepair().rarity(Rarity.EPIC));
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return super.getName(stack).withStyle(ChatFormatting.RED);
	}

	@Override
	public void inventoryTick(ItemStack tear, Level level, Entity entity, int itemSlot, boolean isSelected) {
		if (level.isClientSide || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % COOLDOWN != 0 || !isEnabled(tear) || isInCooldown(tear, level)) {
			return;
		}

		ItemStack tearStack = getStackFromTear(tear);
		if (tearStack.isEmpty()) {
			resetTear(tear);
			return;
		}

		Optional<Integer> experience = Config.COMMON.items.infernalTear.getItemExperience(RegistryHelper.getItemRegistryName(tearStack.getItem()));
		if (experience.isEmpty()) {
			resetTear(tear);
			return;
		}

		int countConsumed = InventoryHelper.consumeItemStack(stack -> ItemStack.isSameItemSameComponents(tearStack, stack), player, 4).getCount();
		if (countConsumed > 0) {
			player.giveExperiencePoints(experience.get() * countConsumed);
		} else {
			setCooldown(tear, level, NOTHING_FOUND_COOLDOWN);
		}
	}

	private void resetTear(ItemStack stack) {
		stack.remove(DataComponents.CONTAINER);
		stack.remove(ModDataComponents.ENABLED);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		if (getStackFromTear(stack).isEmpty()) {
			TooltipBuilder.of(tooltip, context).description("tooltip.reliquary.tear_empty");
		}
	}

	@Override
	protected void addMoreInformation(ItemStack stack, @Nullable HolderLookup.Provider registries, TooltipBuilder tooltipBuilder) {
		ItemStack contents = getStackFromTear(stack);
		String itemName = contents.getHoverName().getString();

		tooltipBuilder.data("tooltip." + Reliquary.MOD_ID + ".tear", itemName);

		if (isEnabled(stack)) {
			tooltipBuilder.absorbActive(itemName);
		}
		tooltipBuilder.description("tooltip." + Reliquary.MOD_ID + ".absorb");
		tooltipBuilder.description(this, ".infernal_tear.absorb_unset");
	}

	@Override
	protected boolean hasMoreInformation(ItemStack stack) {
		return !getStackFromTear(stack).isEmpty();
	}

	public static ItemStack getStackFromTear(ItemStack tear) {
		ItemContainerContents contents = tear.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		return contents.getSlots() > 0 ? contents.getStackInSlot(0) : ItemStack.EMPTY;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
		}
		InteractionResultHolder<ItemStack> actionResult = super.use(level, player, hand);
		ItemStack tear = actionResult.getObject();
		if (player.isShiftKeyDown() && !isEnabled(tear)) {
			return actionResult;
		}


		//empty the tear if player is not sneaking and the tear is not empty
		if (!player.isShiftKeyDown() && !getStackFromTear(tear).isEmpty()) {
			resetTear(tear);
			return actionResult;
		}

		//nothing more to do with a filled tear here
		if (!getStackFromTear(tear).isEmpty()) {
			return actionResult;
		}

		//if user is sneaking or just enabled the tear, let's fill it
		if (player.isShiftKeyDown() || !isEnabled(tear)) {
			IItemHandler playerInventory = InventoryHelper.getMainInventoryItemHandlerFrom(player);
			ItemStack returnStack = buildTear(tear, playerInventory);
			if (!returnStack.isEmpty()) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, returnStack);
			}
		}

		//by this time the tear is still empty and there wasn't anything to put in it
		// so let's disable it if it got enabled
		if (isEnabled(tear)) {
			toggleEnabled(tear);
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

		if (Boolean.TRUE.equals(Config.COMMON.items.infernalTear.absorbWhenCreated.get())) {
			stack.set(ModDataComponents.ENABLED, true);
		}

		return tear;
	}

	public static void setTearTarget(ItemStack tear, ItemStack target) {
		tear.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(target)));
	}

	private ItemStack getTargetAlkahestItem(ItemStack self, IItemHandler inventory) {
		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty() || self.getItem() == stack.getItem() || stack.getMaxStackSize() == 1 || !stack.getComponentsPatch().isEmpty()
					|| Config.COMMON.items.infernalTear.getItemExperience(RegistryHelper.getItemRegistryName(stack.getItem())).isEmpty()) {
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
