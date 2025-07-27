package reliquary.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import reliquary.init.ModDataComponents;
import reliquary.item.component.OversizedComponentItemHandler;
import reliquary.util.InventoryHelper;
import reliquary.util.RandHelper;

import java.util.function.*;

public abstract class ToggleableItem extends ItemBase {
	protected static final int FIRST_SLOT = 0;

	protected ToggleableItem(Properties properties, Supplier<Boolean> isDisabled) {
		super(properties, isDisabled);
	}

	protected ToggleableItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return isEnabled(stack);
	}

	protected void setCooldown(ItemStack stack, Level level, int cooldown) {
		stack.set(ModDataComponents.COOLDOWN_TIME, level.getGameTime() + cooldown);
	}

	protected boolean isInCooldown(ItemStack stack, Level level) {
		return stack.getOrDefault(ModDataComponents.COOLDOWN_TIME, 0L) > level.getGameTime();
	}

	protected <T> T getFromHandler(ItemStack stack, Function<OversizedComponentItemHandler, T> getter) {
		return getter.apply(createHandler(stack));
	}

	protected void runOnHandler(ItemStack stack, Consumer<OversizedComponentItemHandler> runner) {
		runner.accept(createHandler(stack));
	}

	public OversizedComponentItemHandler createHandler(ItemStack stack) {
		int size = Math.max(stack.has(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS) ? stack.get(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS).getSlots() : getContainerInitialSize(), getContainerInitialSize());
		return new OversizedComponentItemHandler(stack, ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS.get(), size, this::getContainerSlotLimit, this::isItemValidForContainerSlot, this::getStackWorth);
	}

	protected int getStackWorth(int slot) {
		return 1;
	}

	protected void removeContainerContents(ItemStack stack) {
		stack.remove(ModDataComponents.OVERSIZED_ITEM_CONTAINER_CONTENTS);
	}

	protected int getContainerInitialSize() {
		return 1;
	}

	protected int getContainerSlotLimit(ItemStack stack, int slot) {
		return getContainerSlotLimit(slot);
	}

	protected int getContainerSlotLimit(int slot) {
		return 64;
	}

	protected boolean isItemValidForContainerSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide && player.isShiftKeyDown()) {
			toggleEnabled(stack);
			player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F * (RandHelper.getRandomMinusOneToOne(player.level().random) * 0.7F + 1.2F));

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem() || oldStack.hasFoil() != newStack.hasFoil();
	}

	public boolean isEnabled(ItemStack stack) {
		return stack.getOrDefault(ModDataComponents.ENABLED, false);
	}

	void toggleEnabled(ItemStack stack) {
		stack.set(ModDataComponents.ENABLED, !isEnabled(stack));
	}

	protected void consumeAndCharge(Player player, int freeCapacity, int chargePerItem, Item item, int maxCount, IntConsumer addCharge) {
		consumeAndCharge(player, freeCapacity, chargePerItem, stack -> stack.getItem() == item, maxCount, addCharge);
	}

	protected void consumeAndCharge(Player player, int freeCapacity, int chargePerItem, Predicate<ItemStack> itemMatches, int maxCount, IntConsumer addCharge) {
		int maximumToConsume = Math.min(freeCapacity / chargePerItem, maxCount);
		if (maximumToConsume == 0) {
			return;
		}
		int chargeToAdd = InventoryHelper.consumeItemStack(itemMatches, player, maximumToConsume).getCount() * chargePerItem;
		if (chargeToAdd > 0) {
			addCharge.accept(chargeToAdd);
		}
	}

	protected boolean addItemToContainer(ItemStack container, Item item, int chargeToAdd) {
		ItemStack stack = new ItemStack(item);
		stack.setCount(chargeToAdd);
		return getFromHandler(container, handler -> handler.insertItemOrAddIntoNewSlotIfNoStackMatches(stack)).isEmpty();
	}

	public boolean removeItemFromInternalStorage(ItemStack stack, int slot, int quantityToRemove, boolean simulate, Player player) {
		if (player.isCreative()) {
			return true;
		}

		return getFromHandler(stack, handler -> handler.extractItemAndRemoveSlotIfEmpty(slot, quantityToRemove, simulate)).getCount() == quantityToRemove;
	}
}
