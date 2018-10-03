package xreliquary.util;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xreliquary.items.ItemToggleable;
import xreliquary.reference.Compatibility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class InventoryHelper {
	private InventoryHelper() {}

	public static void spawnItemStack(World world, double x, double y, double z, @Nonnull ItemStack stack) {
		Random rnd = new Random();

		float f = rnd.nextFloat() * 0.8F + 0.1F;
		float f1 = rnd.nextFloat() * 0.8F + 0.1F;
		float f2 = rnd.nextFloat() * 0.8F + 0.1F;

		EntityItem entityitem = new EntityItem(world, x + (double) f, y + (double) f1, z + (double) f2, stack);

		float f3 = 0.05F;
		entityitem.motionX = rnd.nextGaussian() * (double) f3;
		entityitem.motionY = rnd.nextGaussian() * (double) f3 + 0.20000000298023224D;
		entityitem.motionZ = rnd.nextGaussian() * (double) f3;
		world.spawnEntity(entityitem);
	}

	@Nonnull
	public static ItemStack getTargetItem(@Nonnull ItemStack self, IItemHandler inventory) {
		if (self.isEmpty())
			return ItemStack.EMPTY;

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

	public static int getItemQuantity(@Nonnull ItemStack stack, IItemHandler inventory) {
		if (stack.isEmpty())
			return 0;

		int itemQuantity = 0;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack newStack = inventory.getStackInSlot(slot);
			if (StackHelper.isItemAndNbtEqual(stack, newStack)) {
				itemQuantity += newStack.getCount();
			}
		}
		return itemQuantity;
	}

	public static boolean consumeItem(String itemName, int meta, boolean ignoreMeta, EntityPlayer player) {
		//noinspection ConstantConditions
		return consumeItem(is -> is.getItem().getRegistryName().toString().equals(itemName) && (ignoreMeta || is.getMetadata() == meta), player);
	}

	public static boolean consumeItem(Function<ItemStack, Boolean> itemMatches, EntityPlayer player) {
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if (player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}

			ItemStack slotStack = player.inventory.mainInventory.get(slot);
			//noinspection ConstantConditions
			if (itemMatches.apply(slotStack)) {
				int stackSize = slotStack.getCount();
				if (stackSize > 0) {
					slotStack.shrink(1);
					if (slotStack.getCount() <= 0) {
						player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
					}
					return true;
				}
			}
		}
		return false;
	}

	public static boolean consumeItem(ItemStack item, EntityPlayer player) {
		return consumeItem(item, player, 0, 1);
	}

	public static boolean consumeItem(@Nonnull ItemStack item, EntityPlayer player, int minCount) {
		return consumeItem(item, player, minCount, 1);
	}

	public static boolean consumeItem(@Nonnull ItemStack itemStack, EntityPlayer player, int minCount, int countToConsume) {
		if (player.capabilities.isCreativeMode)
			return true;
		if (itemStack.isEmpty() || countToConsume <= 0)
			return false;

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

		if (itemCount - countToConsume < minCount)
			return false;

		//fill stacks based on which ones have the highest sizes
		if (itemCount >= countToConsume) {
			slotCounts.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

			int countToFill = itemCount - countToConsume;

			for (Map.Entry<Integer, Integer> slotCount : slotCounts) {
				int slot = slotCount.getKey();

				//fill stack sizes up to remaining value
				if (countToFill > 0) {
					int stackSizeToFill = Math.min(itemStack.getMaxStackSize(), countToFill);

					//noinspection ConstantConditions
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

	public static int tryToRemoveFromInventory(@Nonnull ItemStack contents, IItemHandler inventory, int maxToRemove) {
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

			if (remaining <= 0)
				break;
		}
		return maxToRemove - remaining;
	}

	public static Optional<IItemHandler> getInventoryAtPos(World world, BlockPos pos) {
		return getInventoryAtPos(world, pos, null);
	}
	public static Optional<IItemHandler> getInventoryAtPos(World world, BlockPos pos, @Nullable EnumFacing side) {
		return WorldHelper.getTile(world, pos).map(te -> InventoryHelper.getItemHandlerFrom(te, side)).orElse(Optional.empty());
	}

	public static IItemHandler getItemHandlerFrom(EntityPlayer player) {
		return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
	}

	public static Optional<IItemHandler> getItemHandlerFrom(TileEntity te) {
		return getItemHandlerFrom(te, null);
	}

	private static Optional<IItemHandler> getItemHandlerFrom(TileEntity te, @Nullable EnumFacing side) {
		if (hasItemHandler(te, side)) {
			//noinspection ConstantConditions
			return Optional.of(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side));
		}
		return Optional.empty();
	}

	public static int insertIntoInventory(@Nonnull ItemStack contents, IItemHandler inventory) {
		return tryToAddToInventory(contents, inventory, contents.getCount());
	}

	public static int tryToAddToInventory(@Nonnull ItemStack contents, IItemHandler inventory, int maxToAdd) {
		int inventorySize = inventory.getSlots();

		int remaining = maxToAdd;
		ItemStack stackToInsert = contents.copy();
		int currentStackCount = Math.min(remaining, stackToInsert.getMaxStackSize());
		stackToInsert.setCount(currentStackCount);
		for (int slot = 0; slot < inventorySize; slot++) {
			//storage drawers and similar storage blocks support
			while(inventory.insertItem(slot, stackToInsert, true).getCount() < stackToInsert.getCount()) {
				ItemStack remainingStack = inventory.insertItem(slot, stackToInsert, false);
				if (remainingStack.getCount() < currentStackCount) {
					remaining -= (currentStackCount - remainingStack.getCount());
					if (remaining <= 0) {
						break;
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
				if (world.isRemote)
					return;
				EntityItem itemEntity = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, stack);
				world.spawnEntity(itemEntity);
				break;
			}
		}
	}

	public static boolean tryAddingPlayerCurrentItem(EntityPlayer player, IItemHandler inventory, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand).copy();
		stack.setCount(1);

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ItemStack remainingStack = inventory.insertItem(slot, stack, false);
			if (remainingStack.isEmpty()) {
				player.getHeldItem(hand).shrink(1);

				if (player.getHeldItem(hand).getCount() == 0)
					player.setHeldItem(hand, ItemStack.EMPTY);

				player.inventory.markDirty();
				return true;
			}
		}

		return false;
	}

	public static boolean playerHasItem(EntityPlayer player, Item item) {
		return playerHasItem(player, item, false);
	}

	public static boolean playerHasItem(EntityPlayer player, Item item, boolean checkEnabled) {
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack.isEmpty())
				continue;
			if (stack.getItem() == item) {
				return !(checkEnabled && stack.getItem() instanceof ItemToggleable) || ((ItemToggleable) stack.getItem()).isEnabled(stack);
			}
		}

		if (Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for (int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);
				if (!baubleStack.isEmpty() && baubleStack.getItem() == item) {
					return !(checkEnabled && baubleStack.getItem() instanceof ItemToggleable) || ((ItemToggleable) baubleStack.getItem()).isEnabled(baubleStack);
				}
			}
		}

		return false;
	}

	@Nonnull
	public static ItemStack getCorrectItemFromEitherHand(EntityPlayer player, Item item) {
		if (player == null)
			return ItemStack.EMPTY;

		EnumHand itemInHand = getHandHoldingCorrectItem(player, item);

		if (itemInHand == null)
			return ItemStack.EMPTY;

		return player.getHeldItem(itemInHand);
	}

	private static EnumHand getHandHoldingCorrectItem(EntityPlayer player, Item item) {
		if (player.getHeldItemMainhand().getItem() == item) {
			return EnumHand.MAIN_HAND;
		}

		if (player.getHeldItemOffhand().getItem() == item) {
			return EnumHand.OFF_HAND;
		}
		return null;
	}

	public static void addItemToPlayerInventory(EntityPlayer player, ItemStack ist) {
		for (int i = 0; i < player.inventory.mainInventory.size(); ++i) {
			if (player.inventory.getStackInSlot(i).isEmpty()) {
				player.inventory.setInventorySlotContents(i, ist);
				return;
			}
		}
		player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, ist));
	}

	public static NonNullList<ItemStack> getItemStacks(IItemHandler inventory) {
		NonNullList<ItemStack> ret = NonNullList.create();

		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			ret.add(inventory.getStackInSlot(slot));
		}
		return ret;
	}

	public static void dropInventoryItems(World world, BlockPos pos) {
		WorldHelper.getTile(world, pos).ifPresent(te -> getItemHandlerFrom(te).ifPresent(itemHandler -> dropInventoryItems(world, pos, itemHandler)));
	}

	private static void dropInventoryItems(World world, BlockPos pos, IItemHandler inventory) {
		dropInventoryItems(world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), inventory);
	}

	private static void dropInventoryItems(World world, double x, double y, double z, IItemHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				spawnItemStack(world, x, y, z, itemstack);
			}
		}
	}

	public static boolean hasItemHandler(World world, BlockPos pos) {
		return WorldHelper.getTile(world, pos).map(InventoryHelper::hasItemHandler).orElse(false);
	}

	private static boolean hasItemHandler(TileEntity te) {
		return hasItemHandler(te, null);
	}

	private static boolean hasItemHandler(TileEntity te, @Nullable EnumFacing side) {
		return te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	}
}
