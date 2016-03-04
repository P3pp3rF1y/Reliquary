package xreliquary.util;


import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class InventoryHelper {

    public static void removeItem(ItemStack stack, IInventory inventory, int quantity) {
        InventoryHelper.removeItem(stack, inventory, quantity, -1);
    }

    public static void removeItem(ItemStack stack, IInventory inventory, int quantity, int limit) {
        for (int slot = 0; slot < Math.min(inventory.getSizeInventory(), (limit > 0 ? limit : inventory.getSizeInventory())); slot++) {
            ItemStack ist = inventory.getStackInSlot(slot);
            if (ist == null) {
                continue;
            }
            if (StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), stack)) {
                while (quantity > 0 && inventory.getStackInSlot(slot) != null) {
                    inventory.decrStackSize(slot, 1);
                    quantity--;
                }
            }
        }
        inventory.markDirty();
    }

    public static ItemStack getTargetItem(ItemStack self, IInventory inventory, boolean disposeOfItem) {
        ItemStack targetItem = null;
        int itemQuantity = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }
            if (StackHelper.isItemAndNbtEqual(self, stack)) {
                continue;
            }
            if (stack.getMaxStackSize() == 1) {
                continue;
            }
            if (getItemQuantity(stack, inventory) > itemQuantity) {
                itemQuantity = getItemQuantity(stack, inventory);
                targetItem = stack.copy();

                if (disposeOfItem) {
                    inventory.decrStackSize(slot, 1);
                }
            }
        }
        inventory.markDirty();
        return targetItem;
    }

    public static int getItemQuantity(ItemStack stack, IInventory inventory) {
        return InventoryHelper.getItemQuantity(stack, inventory, 0);
    }

    public static int getItemQuantity(ItemStack stack, IInventory inventory, int limit) {
        int itemQuantity = 0;
        for (int slot = 0; slot < Math.min(inventory.getSizeInventory(), (limit > 0 ? limit : inventory.getSizeInventory())); slot++) {
            ItemStack newStack = inventory.getStackInSlot(slot);
            if (newStack == null) {
                continue;
            }
            if (StackHelper.isItemAndNbtEqual(stack, newStack)) {
                itemQuantity += newStack.stackSize;
            }
        }
        return itemQuantity;
    }

    public static boolean consumeItem(Object item, EntityPlayer player) {
        return consumeItem(new Object[]{item}, player, 0, 1);
    }

    public static boolean consumeItem(Object item, EntityPlayer player, int minCount) {
        return consumeItem(new Object[]{item}, player, minCount, 1);
    }

    public static boolean consumeItem(Object item, EntityPlayer player, int minCount, int amountDecreased) {
        return consumeItem(new Object[]{item}, player, minCount, amountDecreased);
    }

    public static boolean consumeItem(Object[] itemList, EntityPlayer player, int minCount, int amountDecreased) {
        if (player.capabilities.isCreativeMode)
            return true;
        if (itemList.length == 0 || !(itemList[0] instanceof ItemStack || itemList[0] instanceof Item || itemList[0] instanceof Block))
            return false;
        List<Integer> suggestedSlots = new ArrayList<Integer>();
        int itemCount = 0;
        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            if (player.inventory.mainInventory[slot] == null) {
                continue;
            }

            ItemStack slotStack = player.inventory.mainInventory[slot];
            for (Object stack : itemList) {
                if ((stack instanceof ItemStack && StackHelper.isItemAndNbtEqual(slotStack, (ItemStack) stack)) ||
                        (stack instanceof Block && RegistryHelper.itemsEqual(Item.getItemFromBlock((Block) stack), slotStack.getItem()) ||
                                (stack instanceof Item && RegistryHelper.itemsEqual((Item) stack, slotStack.getItem())))) {
                    itemCount += player.inventory.mainInventory[slot].stackSize;
                    suggestedSlots.add(slot);
                }
            }
        }
        int count = amountDecreased;
        if (suggestedSlots.size() > 0 && itemCount >= minCount + amountDecreased) {
            for (int slot : suggestedSlots) {
                int stackSize = player.inventory.getStackInSlot(slot).stackSize;
                if (stackSize >= count) {
                    player.inventory.decrStackSize(slot, count);
                    return true;
                } else {
                    player.inventory.decrStackSize(slot, stackSize);
                    count -= stackSize;
                }
            }
        }
        return false;
    }

    public static int tryToRemoveFromInventory(ItemStack contents, IInventory inventory, int maxToRemove) {
        int numberRemoved = 0;

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            //storage drawers compatibility loop
            while (inventory.getStackInSlot(slot) != null && StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents) && maxToRemove > numberRemoved) {
                numberRemoved += Math.min(maxToRemove - numberRemoved, inventory.getStackInSlot(slot).stackSize);
                inventory.decrStackSize(slot, Math.min(maxToRemove, inventory.getStackInSlot(slot).stackSize));
            }

            if (numberRemoved >= maxToRemove)
                return numberRemoved;
        }
        return numberRemoved;
    }

    public static int tryToAddToInventory(ItemStack contents, IInventory inventory, int limit, int maxToAdd) {
        int numberAdded = 0;

        for (int slot = 0; slot < Math.min(inventory.getSizeInventory(), (limit > 0 ? limit : inventory.getSizeInventory())); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                //loop because of storage drawers like inventories
                while (inventory.getStackInSlot(slot) == null && maxToAdd > numberAdded) {
                    ItemStack newContents = contents.copy();
                    int stackAddition = Math.min(newContents.getMaxStackSize(), maxToAdd - numberAdded);
                    newContents.stackSize = stackAddition;
                    inventory.setInventorySlotContents(slot, newContents);
                    numberAdded += stackAddition;
                }
            } else if (StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents)) {
                if (inventory.getStackInSlot(slot).stackSize == inventory.getStackInSlot(slot).getMaxStackSize()) {
                    continue;
                }
                ItemStack slotStack = inventory.getStackInSlot(slot);
                int stackAddition = Math.min(slotStack.getMaxStackSize() - slotStack.stackSize, maxToAdd - numberAdded);
                slotStack.stackSize += stackAddition;
                numberAdded += stackAddition;
            }
            if (numberAdded >= maxToAdd)
                return numberAdded;

        }
        return numberAdded;
    }

    public static void tryRemovingLastStack(IInventory inventory, World worldObj, BlockPos pos) {
        for (int i = inventory.getSizeInventory() - 1; i >= 0; i--) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                inventory.setInventorySlotContents(i, null);
                if (worldObj.isRemote)
                    return;
                inventory.markDirty();
                EntityItem itemEntity = new EntityItem(worldObj, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
                worldObj.spawnEntityInWorld(itemEntity);
                break;
            }
        }
    }

    public static boolean tryAddingPlayerCurrentItem(EntityPlayer player, IInventory inventory) {
        if(inventory.getStackInSlot(0) != null)
            return false;

        inventory.setInventorySlotContents(0, player.getCurrentEquippedItem().copy());

        player.getCurrentEquippedItem().stackSize--;

        if(player.getCurrentEquippedItem().stackSize == 0)
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        return true;
    }
}
