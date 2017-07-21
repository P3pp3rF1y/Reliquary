package xreliquary.util;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.init.ModItems;
import xreliquary.items.ItemToggleable;
import xreliquary.reference.Compatibility;

import javax.annotation.Nonnull;
import java.util.*;

public class InventoryHelper {

	public static void spawnItemStack(World worldIn, double x, double y, double z, @Nonnull ItemStack stack) {
		Random rnd = new Random();

		float f = rnd.nextFloat() * 0.8F + 0.1F;
		float f1 = rnd.nextFloat() * 0.8F + 0.1F;
		float f2 = rnd.nextFloat() * 0.8F + 0.1F;

		while(stack.getCount() > 0) {
			int i = rnd.nextInt(21) + 10;

			if(i > stack.getCount()) {
				i = stack.getCount();
			}

			stack.shrink(i);

			ItemStack stackToDrop = stack.copy();
			stackToDrop.setCount(i);
			EntityItem entityitem = new EntityItem(worldIn, x + (double) f, y + (double) f1, z + (double) f2, stackToDrop);

			float f3 = 0.05F;
			entityitem.motionX = rnd.nextGaussian() * (double) f3;
			entityitem.motionY = rnd.nextGaussian() * (double) f3 + 0.20000000298023224D;
			entityitem.motionZ = rnd.nextGaussian() * (double) f3;
			worldIn.spawnEntity(entityitem);
		}
	}

	@Nonnull
	public static ItemStack getTargetItem(@Nonnull ItemStack self, IInventory inventory) {
		if (self.isEmpty())
			return ItemStack.EMPTY;

		ItemStack targetItem = ItemStack.EMPTY;
		int itemQuantity = 0;
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if(StackHelper.isItemAndNbtEqual(self, stack)) {
				continue;
			}
			if(stack.getMaxStackSize() == 1) {
				continue;
			}
			if(getItemQuantity(stack, inventory) > itemQuantity) {
				itemQuantity = getItemQuantity(stack, inventory);
				targetItem = stack.copy();
			}
		}
		inventory.markDirty();
		return targetItem;
	}

	public static int getItemQuantity(@Nonnull ItemStack stack, IInventory inventory) {
		if (stack.isEmpty())
			return 0;

		int itemQuantity = 0;
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack newStack = inventory.getStackInSlot(slot);
			if(StackHelper.isItemAndNbtEqual(stack, newStack)) {
				itemQuantity += newStack.getCount();
			}
		}
		return itemQuantity;
	}

	public static boolean consumeOreDictItem(String oredictName, EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if(player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}

			ItemStack slotStack = player.inventory.mainInventory.get(slot);
			for(ItemStack ore : OreDictionary.getOres(oredictName)) {
				if(OreDictionary.itemMatches(ore, slotStack, false)) {
					int stackSize = slotStack.getCount();
					if(stackSize > 0) {
						slotStack.shrink(1);
						if (slotStack.getCount() <= 0) {
							player.inventory.mainInventory.set(slot, ItemStack.EMPTY);
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean consumeItem(String itemName, int meta, boolean ignoreMeta, EntityPlayer player) {
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			if(player.inventory.mainInventory.get(slot).isEmpty()) {
				continue;
			}

			ItemStack slotStack = player.inventory.mainInventory.get(slot);
			//noinspection ConstantConditions
			if(slotStack.getItem().getRegistryName().toString().equals(itemName) && (ignoreMeta || slotStack.getMetadata() == meta)) {
				int stackSize = slotStack.getCount();
				if(stackSize > 0) {
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
		if(player.capabilities.isCreativeMode)
			return true;
		if(itemStack.isEmpty() || countToConsume <= 0)
			return false;

		int itemCount = 0;

		List<Map.Entry<Integer, Integer>> slotCounts = new ArrayList<>();
		for(int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack slotStack = player.inventory.mainInventory.get(slot);
			if(StackHelper.isItemAndNbtEqual(slotStack, itemStack)) {
				int stackSize = slotStack.getCount();
				itemCount += stackSize;
				slotCounts.add(new AbstractMap.SimpleEntry<>(slot, stackSize));
			}
		}

		if(itemCount - countToConsume < minCount)
			return false;

		//fill stacks based on which ones have the highest sizes
		if(itemCount >= countToConsume) {
			slotCounts.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

			int countToFill = itemCount - countToConsume;

			for(Map.Entry<Integer, Integer> slotCount : slotCounts) {
				int slot = slotCount.getKey();

				//fill stack sizes up to remaining value
				if(countToFill > 0) {
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

	public static int tryToRemoveFromInventory(@Nonnull ItemStack contents, IInventory inventory, int maxToRemove) {
		int numberRemoved = 0;

		for(int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			if(inventory.getStackInSlot(slot).isEmpty()) {
				continue;
			}
			//storage drawers compatibility loop
			while(inventory.getStackInSlot(slot).getCount() > 0 && StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents) && maxToRemove > numberRemoved) {
				//noinspection ConstantConditions
				numberRemoved += Math.min(maxToRemove - numberRemoved, inventory.getStackInSlot(slot).getCount());
				//noinspection ConstantConditions
				inventory.decrStackSize(slot, Math.min(maxToRemove, inventory.getStackInSlot(slot).getCount()));
			}

			if(numberRemoved >= maxToRemove)
				return numberRemoved;
		}
		return numberRemoved;
	}

	public static int tryToAddToInventory(@Nonnull ItemStack contents, IInventory inventory, int maxToAdd) {
		return tryToAddToInventory(contents, inventory, maxToAdd, EnumFacing.UP);
	}

	public static int tryToAddToInventory(@Nonnull ItemStack contents, IInventory inventory, int maxToAdd, EnumFacing insertDirection) {
		int numberAdded = 0;

		if(inventory instanceof ISidedInventory) {
			ISidedInventory sidedInventory = (ISidedInventory) inventory;

			int[] slotsForFace = sidedInventory.getSlotsForFace(insertDirection);
			for(int slot : slotsForFace) {
				if(sidedInventory.getStackInSlot(slot).isEmpty() && sidedInventory.canInsertItem(slot, contents, insertDirection)) {
					numberAdded += insertIntoEmptySlot(contents, sidedInventory, slot, maxToAdd - numberAdded);
				} else if(StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents)) {
					//noinspection ConstantConditions
					if(inventory.getStackInSlot(slot).getCount() == Math.min(inventory.getStackInSlot(slot).getMaxStackSize(), inventory.getInventoryStackLimit())) {
						continue;
					}
					int stackAddition = addToNonEmptySlot(inventory, maxToAdd - numberAdded, slot);
					numberAdded += stackAddition;
				}
				if(numberAdded >= maxToAdd)
					break;
			}

		} else {
			int inventorySize = inventory instanceof InventoryPlayer ? ((InventoryPlayer) inventory).mainInventory.size() : inventory.getSizeInventory();

			for(int slot = 0; slot < inventorySize; slot++) {
				if(inventory.getStackInSlot(slot).isEmpty() && inventory.isItemValidForSlot(slot, contents)) {
					numberAdded += insertIntoEmptySlot(contents, inventory, slot, maxToAdd - numberAdded);
				} else if(StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents)) {
					//noinspection ConstantConditions
					if(inventory.getStackInSlot(slot).getCount() == Math.min(inventory.getStackInSlot(slot).getMaxStackSize(), inventory.getInventoryStackLimit())) {
						continue;
					}
					int stackAddition = addToNonEmptySlot(inventory, maxToAdd - numberAdded, slot);
					numberAdded += stackAddition;
				}
				if(numberAdded >= maxToAdd)
					break;
			}
		}

		if(numberAdded > 0)
			inventory.markDirty();

		return numberAdded;
	}

	private static int addToNonEmptySlot(IInventory inventory, int maxToAdd, int slot) {
		ItemStack slotStack = inventory.getStackInSlot(slot);
		//noinspection ConstantConditions
		int stackAddition = Math.min(Math.min(slotStack.getMaxStackSize(), inventory.getInventoryStackLimit()) - slotStack.getCount(), maxToAdd);
		slotStack.grow(stackAddition);
		return stackAddition;
	}

	private static int insertIntoEmptySlot(@Nonnull ItemStack contents, IInventory inventory, int slot, int maxToAdd) {
		int numberAdded = 0;
		//loop because of storage drawers like inventories
		while(inventory.getStackInSlot(slot).isEmpty() && maxToAdd > numberAdded) {
			int stackAddition = Math.min(Math.min(contents.getMaxStackSize(), inventory.getInventoryStackLimit()), maxToAdd - numberAdded);
			ItemStack newContents = contents.copy();
			newContents.setCount(stackAddition);
			inventory.setInventorySlotContents(slot, newContents);
			numberAdded += stackAddition;
		}
		return numberAdded;
	}

	private static int insertIntoEmptySlot(@Nonnull ItemStack contents, ISidedInventory inventory, int slot, int maxToAdd) {
		int numberAdded = 0;
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		//loop because of storage drawers like inventories
		while((stackInSlot.isEmpty() || stackInSlot.getCount() < stackInSlot.getMaxStackSize()) && maxToAdd > numberAdded) {
			int maxSlotAddition = stackInSlot.isEmpty() ? contents.getMaxStackSize() : stackInSlot.getMaxStackSize() - stackInSlot.getCount();
			int stackAddition = Math.min(maxSlotAddition, maxToAdd - numberAdded);
			ItemStack newContents = contents.copy();
			newContents.setCount(stackAddition);
			inventory.setInventorySlotContents(slot, newContents);
			numberAdded += stackAddition;
			stackInSlot = inventory.getStackInSlot(slot);
		}
		return numberAdded;
	}

	public static void tryRemovingLastStack(IInventory inventory, World world, BlockPos pos) {
		for(int i = inventory.getSizeInventory() - 1; i >= 0; i--) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(!stack.isEmpty()) {
				inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				if(world.isRemote)
					return;
				inventory.markDirty();
				EntityItem itemEntity = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D, stack);
				world.spawnEntity(itemEntity);
				break;
			}
		}
	}

	public static boolean tryAddingPlayerCurrentItem(EntityPlayer player, IInventory inventory, EnumHand hand) {
		if(!inventory.getStackInSlot(0).isEmpty())
			return false;

		ItemStack stack = player.getHeldItem(hand).copy();
		stack.setCount(1);

		//noinspection ConstantConditions
		inventory.setInventorySlotContents(0, stack);

		player.getHeldItem(hand).shrink(1);

		if(player.getHeldItem(hand).getCount() == 0)
			player.setHeldItem(hand, ItemStack.EMPTY);

		player.inventory.markDirty();
		return true;
	}

	public static boolean playerHasItem(EntityPlayer player, Item item) {
		return playerHasItem(player, item, false);
	}

	public static boolean playerHasItem(EntityPlayer player, Item item, boolean checkEnabled) {
		for(ItemStack stack : player.inventory.mainInventory) {
			if(stack.isEmpty())
				continue;
			if(stack.getItem() == item) {
				if(checkEnabled) {
					if(stack.getItem() instanceof ItemToggleable) {
						return ((ItemToggleable) stack.getItem()).isEnabled(stack);
					}
				}
				return true;
			}
		}

		if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < inventoryBaubles.getSlots(); i++) {
				ItemStack baubleStack = inventoryBaubles.getStackInSlot(i);
				if(!baubleStack.isEmpty() && baubleStack.getItem() == item) {
					if(checkEnabled) {
						if(baubleStack.getItem() instanceof ItemToggleable) {
							return ((ItemToggleable) baubleStack.getItem()).isEnabled(baubleStack);
						}
					}
					return true;
				}
			}
		}

		return false;
	}
}
