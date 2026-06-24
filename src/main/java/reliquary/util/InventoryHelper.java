package reliquary.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import reliquary.item.ToggleableItem;

import javax.annotation.Nullable;

import java.util.*;
import java.util.function.*;

public class InventoryHelper {
	private InventoryHelper() {
	}

	public static void spawnItemStack(Level level, BlockPos pos, ItemStack stack) {
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
	}

	public static ItemStack getTargetItem(ItemStack self, ResourceHandler<ItemResource> inventory) {
		if (self.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.size(); slot++) {
			ItemStack stack = inventory.getResource(slot).toStack(inventory.getAmountAsInt(slot));
			if (ItemStack.isSameItemSameComponents(self, stack) || stack.getMaxStackSize() == 1) {
				continue;
			}
			int qty = getItemQuantity(stack, inventory);
			if (qty > itemQuantity) {
				itemQuantity = qty;
				targetItem = stack.copy();
			}
		}
		return targetItem;
	}

	public static int getItemQuantity(ItemStack stack, ResourceHandler<ItemResource> inventory) {
		if (stack.isEmpty()) {
			return 0;
		}

		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.size(); slot++) {
			ItemStack newStack = inventory.getResource(slot).toStack(inventory.getAmountAsInt(slot));
			if (ItemStack.isSameItemSameComponents(stack, newStack)) {
				itemQuantity += newStack.getCount();
			}
		}
		return itemQuantity;
	}

	public static ItemStack consumeItemStack(Predicate<ItemResource> itemMatches, Player player, int count) {
		return extractFromInventory(itemMatches, count, getMainInventoryItemHandlerFrom(player));
	}

	public static ItemStack extractFromInventory(Predicate<ItemResource> itemMatches, int count, ResourceHandler<ItemResource> inventory) {
		try (var tx = Transaction.openRoot()) {
			ResourceStack<ItemResource> rs = ResourceHandlerUtil.extractFirst(inventory, itemMatches, count, tx);
			if (rs == null) {
				return ItemStack.EMPTY;
			}
			tx.commit();
			return rs.resource().toStack(rs.amount());
		}
	}

	public static boolean consumeItem(ItemStack itemStack, Player player, int minCount, int countToConsume) {
		if (player.isCreative()) {
			return true;
		}
		if (itemStack.isEmpty() || countToConsume <= 0) {
			return false;
		}

		int itemCount = 0;

		List<Map.Entry<Integer, Integer>> slotCounts = new ArrayList<>();
		for (int slot = 0; slot < player.getInventory().getNonEquipmentItems().size(); slot++) {
			ItemStack slotStack = player.getInventory().getNonEquipmentItems().get(slot);
			if (ItemStack.isSameItemSameComponents(slotStack, itemStack)) {
				int stackSize = slotStack.getCount();
				itemCount += stackSize;
				slotCounts.add(new AbstractMap.SimpleEntry<>(slot, stackSize));
			}
		}

		if (itemCount - countToConsume < minCount) {
			return false;
		}

		// fill stacks based on which ones have the highest sizes
		if (itemCount >= countToConsume) {
			slotCounts.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

			int countToFill = itemCount - countToConsume;

			for (Map.Entry<Integer, Integer> slotCount : slotCounts) {
				int slot = slotCount.getKey();

				// fill stack sizes up to remaining value
				if (countToFill > 0) {
					int stackSizeToFill = Math.min(itemStack.getMaxStackSize(), countToFill);

					player.getInventory().getItem(slot).setCount(stackSizeToFill);

					countToFill -= stackSizeToFill;
				} else {
					player.getInventory().removeItem(slot, player.getInventory().getItem(slot).getCount());
				}
			}
			return true;
		}

		return false;
	}

	public static int tryToRemoveFromInventory(ItemStack contents, ResourceHandler<ItemResource> inventory, int maxToRemove) {
		int remaining = maxToRemove;

		try (var tx = Transaction.openRoot()) {
			for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
				ItemStack s = inventory.getResource(slot).toStack(inventory.getAmountAsInt(slot));
				if (s.isEmpty() || !ItemStack.isSameItemSameComponents(s, contents))
					continue;
				while (remaining > 0) {
					int toExtract = Math.min(remaining, s.getCount());
					int moved = inventory.extract(slot, ItemResource.of(s), toExtract, tx);
					if (moved <= 0)
						break; // handler refused
					remaining -= moved;
					s = inventory.getResource(slot).toStack(inventory.getAmountAsInt(slot)); // refresh view
				}
			}
			if (remaining < maxToRemove)
				tx.commit();
		}
		return maxToRemove - remaining;
	}

	public static void runOnInventoryAt(Level level, BlockPos pos, Consumer<ResourceHandler<ItemResource>> run) {
		ResourceHandler<ItemResource> itemHandler = getInventoryAtPos(level, pos, null);
		if (itemHandler == null) {
			return;
		}
		run.accept(itemHandler);
	}

	@Nullable
	public static ResourceHandler<ItemResource> getInventoryAtPos(Level level, BlockPos pos, @Nullable Direction side) {
		return level.getCapability(Capabilities.Item.BLOCK, pos, side);
	}

	@Nullable
	public static ResourceHandler<ItemResource> getItemHandlerFrom(Player player) {
		return player.getCapability(Capabilities.Item.ENTITY);
	}

	public static ResourceHandler<ItemResource> getMainInventoryItemHandlerFrom(Player player) {
		return RangedResourceHandler.of(PlayerInventoryWrapper.of(player), 0, 36);
	}

	public static void executeOnItemHandlerAt(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity,
			Consumer<ResourceHandler<ItemResource>> run) {
		executeOnItemHandlerAt(level, pos, state, blockEntity, handler -> {
			run.accept(handler);
			return null;
		}, null);
	}

	public static <T> T executeOnItemHandlerAt(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity,
			Function<ResourceHandler<ItemResource>, T> run, @Nullable T defaultReturnValue) {
		return executeOnItemHandlerAt(level, pos, state, blockEntity, null, run, defaultReturnValue);
	}

	private static <T> T executeOnItemHandlerAt(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side,
			Function<ResourceHandler<ItemResource>, T> run, @Nullable T defaultReturnValue) {
		ResourceHandler<ItemResource> itemHandler = level.getCapability(Capabilities.Item.BLOCK, pos, state, blockEntity, side);

		if (itemHandler != null) {
			return run.apply(itemHandler);
		}

		// noinspection DataFlowIssue - sometimes null may be produced based on default value being null, ignoring here not to have to deal with nullability
		// check everywhere this is used
		return defaultReturnValue;
	}

	public static int insertIntoInventory(ItemStack contents, ResourceHandler<ItemResource> inventory) {
		return insertIntoInventory(contents, inventory, contents.getCount());
	}

	public static int insertIntoInventory(ItemStack contents, ResourceHandler<ItemResource> inventory, int amount) {
		if (contents.isEmpty() || amount <= 0) {
			return 0;
		}
		return ResourceHandlerUtil.insertStacking(inventory, ItemResource.of(contents), amount, null);
	}

	public static int tryToAddToInventoryAtPos(ItemStack contents, Level level, BlockPos pos, Direction side, int maxToAdd) {
		ResourceHandler<ItemResource> inventory = getInventoryAtPos(level, pos, side);
		if (inventory == null) {
			return 0;
		}

		return insertIntoInventory(contents, inventory, maxToAdd);
	}

	public static int insertIntoInventoryWithOversizedSupport(ItemStack contents, ResourceHandler<ItemResource> inventory, int maxToAdd) {
		if (contents.isEmpty() || maxToAdd <= 0) {
			return 0;
		}
		int remaining = maxToAdd;
		ItemResource res = ItemResource.of(contents);
		for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
			// storage drawers and similar: try repeatedly while something can go in
			while (remaining > 0) {
				int attempt = Math.min(remaining, contents.getMaxStackSize());
				try (var tx = Transaction.openRoot()) {
					int moved = inventory.insert(slot, res, attempt, tx);
					if (moved <= 0)
						break; // can't insert here
					tx.commit();
					remaining -= moved;
				}
			}
		}
		return maxToAdd - remaining;
	}

	public static void tryRemovingLastStack(ResourceHandler<ItemResource> inventory, Level level, BlockPos pos) {
		for (int i = inventory.size() - 1; i >= 0; i--) {
			ItemStack peek = inventory.getResource(i).toStack(inventory.getAmountAsInt(i));
			if (!peek.isEmpty()) {
				try (var tx = Transaction.openRoot()) {
					int moved = inventory.extract(i, ItemResource.of(peek), peek.getCount(), tx);
					if (moved > 0) {
						tx.commit();
						if (level.isClientSide())
							return;
						ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, peek.copyWithCount(moved));
						level.addFreshEntity(itemEntity);
					}
				}
				break;
			}
		}
	}

	public static boolean tryAddingPlayerCurrentItem(Player player, ResourceHandler<ItemResource> inventory, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand).copy();
		if (stack.isEmpty()) {
			return false;
		}
		stack.setCount(1);
		ItemResource res = ItemResource.of(stack);
		for (int slot = 0; slot < inventory.size(); slot++) {
			try (var tx = Transaction.openRoot()) {
				int moved = inventory.insert(slot, res, 1, tx);
				if (moved == 1) {
					tx.commit();
					player.getItemInHand(hand).shrink(1);
					if (player.getItemInHand(hand).isEmpty()) {
						player.setItemInHand(hand, ItemStack.EMPTY);
					}
					player.getInventory().setChanged();
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack getItemFromAllPlayerHandlers(Player player, Item item) {
		return PlayerInventoryProvider.get().getFromPlayerInventoryHandlers(player, (stack, result) -> {
			if (stack.isEmpty() || stack.getItem() != item) {
				return ItemStack.EMPTY;
			}

			return stack;
		}, result -> !result.isEmpty(), () -> ItemStack.EMPTY);
	}

	public static boolean playerHasItem(Player player, Item item) {
		return playerHasItem(player, item, false);
	}

	public static boolean playerHasItem(Player player, Item item, boolean checkEnabled) {
		return PlayerInventoryProvider.get().getFromPlayerInventoryHandlers(player, (stack, result) -> {
			if (stack.isEmpty()) {
				return false;
			}
			return stack.getItem() == item
					&& (!(checkEnabled && stack.getItem() instanceof ToggleableItem) || ((ToggleableItem) stack.getItem()).isEnabled(stack));
		}, result -> result, () -> false);
	}

	public static ItemStack getCorrectItemFromEitherHand(Player player, Item item) {
		return getHandHoldingCorrectItem(player, item).map(player::getItemInHand).orElse(ItemStack.EMPTY);
	}

	private static Optional<InteractionHand> getHandHoldingCorrectItem(Player player, Item item) {
		if (player.getMainHandItem().getItem() == item) {
			return Optional.of(InteractionHand.MAIN_HAND);
		}

		if (player.getOffhandItem().getItem() == item) {
			return Optional.of(InteractionHand.OFF_HAND);
		}
		return Optional.empty();
	}

	public static void addItemToPlayerInventory(Player player, ItemStack stack) {
		for (int i = 0; i < player.getInventory().getNonEquipmentItems().size(); ++i) {
			if (player.getInventory().getItem(i).isEmpty()) {
				player.getInventory().setItem(i, stack);
				return;
			}
		}
		player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack));
	}

	public static void dropInventoryItems(Level level, BlockPos pos, ResourceHandler<ItemResource> inventory) {
		dropInventoryItems(level, pos.getX(), pos.getY(), pos.getZ(), inventory);
	}

	private static void dropInventoryItems(Level level, double x, double y, double z, ResourceHandler<ItemResource> inventory) {
		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack itemstack = inventory.getResource(i).toStack(inventory.getAmountAsInt(i));
			if (!itemstack.isEmpty()) {
				Containers.dropItemStack(level, x, y, z, itemstack);
			}
		}
	}

	public static boolean hasItemHandler(Level level, BlockPos pos) {
		return executeOnItemHandlerAt(level, pos, level.getBlockState(pos), null, handler -> true, false);
	}

	public static void setSlot(ResourceHandler<ItemResource> h, int slot, ItemStack desired) {
		try (Transaction tx = Transaction.openRoot()) {
			h.extract(slot, h.getResource(slot), h.getAmountAsInt(slot), tx);
			if (!desired.isEmpty()) {
				h.insert(ItemResource.of(desired), desired.getCount(), tx);
			}
			tx.commit();
		}
	}

	public static void iteratePlayerInventory(Player player, BiConsumer<Integer, ItemStack> actOn) {
		iteratePlayerInventory(player, (slot, stack) -> {
			actOn.accept(slot, stack);
			return null;
		}, () -> null, result -> false);
	}

	public static <T> T iteratePlayerInventory(Player player, BiFunction<Integer, ItemStack, T> actOn, Supplier<T> supplyDefault, Predicate<T> shouldExit) {
		T result = supplyDefault.get();
		for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
			result = actOn.apply(slot, player.getInventory().getItem(slot));
			if (shouldExit.test(result)) {
				break;
			}
		}
		return result;
	}
}
