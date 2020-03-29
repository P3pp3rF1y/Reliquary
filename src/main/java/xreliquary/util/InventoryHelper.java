package xreliquary.util;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xreliquary.items.IBaubleItem;
import xreliquary.items.ToggleableItem;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventoryHelper {
	private InventoryHelper() {}

	private static Set<BiFunction<PlayerEntity, IBaubleItem.Type, IItemHandler>> baublesItemHandlerFactories = new HashSet<>();

	public static void addBaublesItemHandlerFactory(BiFunction<PlayerEntity, IBaubleItem.Type, IItemHandler> factory) {
		baublesItemHandlerFactories.add(factory);
	}

	public static void spawnItemStack(World world, BlockPos pos, ItemStack stack) {
		net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	}

	public static ItemStack getTargetItem(ItemStack self, IItemHandler inventory) {
		if (self.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (StackHelper.isItemAndNbtEqual(self, stack) || stack.getMaxStackSize() == 1) {
				continue;
			}
			if (getItemQuantity(stack, inventory) > itemQuantity) {
				itemQuantity = getItemQuantity(stack, inventory);
				targetItem = stack.copy();
			}
		}
		return targetItem;
	}

	public static int getItemQuantity(ItemStack stack, IItemHandler inventory) {
		if (stack.isEmpty()) {
			return 0;
		}

		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack newStack = inventory.getStackInSlot(slot);
			if (StackHelper.isItemAndNbtEqual(stack, newStack)) {
				itemQuantity += newStack.getCount();
			}
		}
		return itemQuantity;
	}

	public static boolean consumeItem(Predicate<ItemStack> itemMatches, PlayerEntity player) {
		return !consumeItemStack(itemMatches, player).isEmpty();
	}

	public static ItemStack consumeItemStack(Predicate<ItemStack> itemMatches, PlayerEntity player) {
		ItemStack ret = ItemStack.EMPTY;
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}

			ItemStack slotStack = player.inventory.mainInventory.get(slot);
			if (itemMatches.test(slotStack)) {
				int stackSize = slotStack.getCount();
				if (stackSize > 0) {
					ret = ItemHandlerHelper.copyStackWithSize(slotStack, 1);
					slotStack.shrink(1);
					if (slotStack.getCount() <= 0) {
						player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
					}
					return ret;
				}
			}
		}
		return ret;
	}

	public static boolean consumeItem(ItemStack item, PlayerEntity player) {
		return consumeItem(item, player, 0, 1);
	}

	public static boolean consumeItem(ItemStack item, PlayerEntity player, int minCount) {
		return consumeItem(item, player, minCount, 1);
	}

	public static boolean consumeItem(ItemStack itemStack, PlayerEntity player, int minCount, int countToConsume) {
		if (player.isCreative()) {
			return true;
		}
		if (itemStack.isEmpty() || countToConsume <= 0) {
			return false;
		}

		int itemCount = 0;

		List<Map.Entry<Integer, Integer>> slotCounts = new ArrayList<>();
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack slotStack = player.inventory.mainInventory.get(slot);
			if (StackHelper.isItemAndNbtEqual(slotStack, itemStack)) {
				int stackSize = slotStack.getCount();
				itemCount += stackSize;
				slotCounts.add(new AbstractMap.SimpleEntry<>(slot, stackSize));
			}
		}

		if (itemCount - countToConsume < minCount) {
			return false;
		}

		//fill stacks based on which ones have the highest sizes
		if (itemCount >= countToConsume) {
			slotCounts.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

			int countToFill = itemCount - countToConsume;

			for (Map.Entry<Integer, Integer> slotCount : slotCounts) {
				int slot = slotCount.getKey();

				//fill stack sizes up to remaining value
				if (countToFill > 0) {
					int stackSizeToFill = Math.min(itemStack.getMaxStackSize(), countToFill);

					player.inventory.getStackInSlot(slot).setCount(stackSizeToFill);

					countToFill -= stackSizeToFill;
				} else {
					player.inventory.decrStackSize(slot, player.inventory.getStackInSlot(slot).getCount());
				}
			}
			return true;
		}

		return false;
	}

	public static int tryToRemoveFromInventory(ItemStack contents, IItemHandler inventory, int maxToRemove) {
		int remaining = maxToRemove;

		ItemStack stackToExtract = contents.copy();
		int currentStackCount = Math.min(remaining, stackToExtract.getMaxStackSize());
		stackToExtract.setCount(currentStackCount);

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			if (inventory.getStackInSlot(slot).isEmpty()) {
				continue;
			}

			//storage drawers compatibility loop
			while (inventory.getStackInSlot(slot).getCount() > 0 && StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents) && remaining > 0) {
				ItemStack extractedStack = inventory.extractItem(slot, Math.min(maxToRemove, inventory.getStackInSlot(slot).getCount()), false);
				if (extractedStack.getCount() == 0) {
					break; //just in case some item handler shows stacks that can't be extracted
				}

				remaining -= extractedStack.getCount();

				stackToExtract = contents.copy();
				currentStackCount = Math.min(remaining, stackToExtract.getMaxStackSize());
				stackToExtract.setCount(currentStackCount);
			}

			if (remaining <= 0) {
				break;
			}
		}
		return maxToRemove - remaining;
	}

	public static LazyOptional<IItemHandler> getInventoryAtPos(World world, BlockPos pos) {
		return getInventoryAtPos(world, pos, null);
	}

	public static LazyOptional<IItemHandler> getInventoryAtPos(World world, BlockPos pos, @Nullable Direction side) {
		return WorldHelper.getTile(world, pos).map(te -> InventoryHelper.getItemHandlerFrom(te, side)).orElse(LazyOptional.empty());
	}

	public static LazyOptional<IItemHandler> getItemHandlerFrom(PlayerEntity player, @Nullable Direction side) {
		return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	}

	public static LazyOptional<IItemHandler> getItemHandlerFrom(PlayerEntity player) {
		return getItemHandlerFrom(player, Direction.UP);
	}

	public static LazyOptional<IItemHandler> getItemHandlerFrom(TileEntity te) {
		return getItemHandlerFrom(te, null);
	}

	private static LazyOptional<IItemHandler> getItemHandlerFrom(TileEntity te, @Nullable Direction side) {
		return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	}

	public static int insertIntoInventory(ItemStack contents, IItemHandler inventory) {
		return tryToAddToInventory(contents, inventory, contents.getCount());
	}

	public static int tryToAddToInventory(ItemStack contents, IItemHandler inventory, int maxToAdd) {
		int inventorySize = inventory.getSlots();

		int remaining = maxToAdd;
		ItemStack stackToInsert = contents.copy();
		int currentStackCount = Math.min(remaining, stackToInsert.getMaxStackSize());
		stackToInsert.setCount(currentStackCount);
		for (int slot = 0; slot < inventorySize; slot++) {
			//storage drawers and similar storage blocks support
			while (inventory.insertItem(slot, stackToInsert, true).getCount() < stackToInsert.getCount()) {
				ItemStack remainingStack = inventory.insertItem(slot, stackToInsert, false);
				if (remainingStack.getCount() < currentStackCount) {
					remaining -= (currentStackCount - remainingStack.getCount());
					if (remaining <= 0) {
						return maxToAdd;
					}
					stackToInsert = contents.copy();
					currentStackCount = Math.min(remaining, stackToInsert.getMaxStackSize());
					stackToInsert.setCount(currentStackCount);
				}
			}
		}

		return maxToAdd - remaining;
	}

	public static void tryRemovingLastStack(IItemHandler inventory, World world, BlockPos pos) {
		for (int i = inventory.getSlots() - 1; i >= 0; i--) {
			if (!inventory.getStackInSlot(i).isEmpty()) {
				ItemStack stack = inventory.getStackInSlot(i).copy();
				inventory.extractItem(i, stack.getCount(), false);
				if (world.isRemote) {
					return;
				}
				ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, stack);
				world.addEntity(itemEntity);
				break;
			}
		}
	}

	public static boolean tryAddingPlayerCurrentItem(PlayerEntity player, IItemHandler inventory, Hand hand) {
		ItemStack stack = player.getHeldItem(hand).copy();
		stack.setCount(1);

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack remainingStack = inventory.insertItem(slot, stack, false);
			if (remainingStack.isEmpty()) {
				player.getHeldItem(hand).shrink(1);

				if (player.getHeldItem(hand).getCount() == 0) {
					player.setHeldItem(hand, ItemStack.EMPTY);
				}

				player.inventory.markDirty();
				return true;
			}
		}

		return false;
	}

	public static boolean playerHasItem(PlayerEntity player, Item item) {
		return playerHasItem(player, item, false, IBaubleItem.Type.NONE);
	}

	public static boolean playerHasItem(PlayerEntity player, Item item, boolean checkEnabled, IBaubleItem.Type baubleType) {
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.getItem() == item) {
				return !(checkEnabled && stack.getItem() instanceof ToggleableItem) || ((ToggleableItem) stack.getItem()).isEnabled(stack);
			}
		}

		return hasItemInBaubleInventories(player, item, checkEnabled, baubleType);
	}

	private static boolean hasItemInBaubleInventories(PlayerEntity player, Item item, boolean checkEnabled, IBaubleItem.Type baubleType) {
		for (BiFunction<PlayerEntity, IBaubleItem.Type, IItemHandler> factory : baublesItemHandlerFactories) {
			IItemHandler handler = factory.apply(player, baubleType);
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack baubleStack = handler.getStackInSlot(i);
				if (!baubleStack.isEmpty() && baubleStack.getItem() == item &&
						(!(checkEnabled && baubleStack.getItem() instanceof ToggleableItem) || ((ToggleableItem) baubleStack.getItem()).isEnabled(baubleStack))) {
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack getCorrectItemFromEitherHand(PlayerEntity player, Item item) {
		return getHandHoldingCorrectItem(player, item).map(player::getHeldItem).orElse(ItemStack.EMPTY);
	}

	private static Optional<Hand> getHandHoldingCorrectItem(PlayerEntity player, Item item) {
		if (player.getHeldItemMainhand().getItem() == item) {
			return Optional.of(Hand.MAIN_HAND);
		}

		if (player.getHeldItemOffhand().getItem() == item) {
			return Optional.of(Hand.OFF_HAND);
		}
		return Optional.empty();
	}

	public static void addItemToPlayerInventory(PlayerEntity player, ItemStack stack) {
		for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
			if (player.inventory.getStackInSlot(i).isEmpty()) {
				player.inventory.setInventorySlotContents(i, stack);
				return;
			}
		}
		player.world.addEntity(new ItemEntity(player.world, player.posX, player.posY, player.posZ, stack));
	}

	public static NonNullList<ItemStack> getItemStacks(IItemHandler inventory) {
		NonNullList<ItemStack> ret = NonNullList.create();

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ret.add(inventory.getStackInSlot(slot));
		}
		return ret;
	}

	public static void dropInventoryItems(World world, BlockPos pos, IItemHandler inventory) {
		dropInventoryItems(world, pos.getX(), pos.getY(), pos.getZ(), inventory);
	}

	private static void dropInventoryItems(World world, double x, double y, double z, IItemHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				net.minecraft.inventory.InventoryHelper.spawnItemStack(world, x, y, z, itemstack);
			}
		}
	}

	public static boolean hasItemHandler(World world, BlockPos pos) {
		return WorldHelper.getTile(world, pos).map(InventoryHelper::hasItemHandler).orElse(false);
	}

	private static boolean hasItemHandler(TileEntity te) {
		return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent();
	}

	public static <T extends IItemHandler> void runOnItemHandler(ItemStack stack, Consumer<T> run, Class<T> itemHandlerClass) {
		getItemHandler(stack, itemHandlerClass).ifPresent(run::accept);
	}

	private static <T extends IItemHandler> LazyOptional<T> getItemHandler(ItemStack stack, Class<T> itemHandlerClass) {
		//noinspection NullableProblems
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).filter(itemHandlerClass::isInstance).map(itemHandlerClass::cast);
	}

	public static <R, T extends IItemHandler> Optional<R> getFromHandler(ItemStack stack, Function<T, R> get, Class<T> itemHandlerClass) {
		return getItemHandler(stack, itemHandlerClass).map(h -> Optional.of(get.apply(h))).orElse(Optional.empty());
	}
}
