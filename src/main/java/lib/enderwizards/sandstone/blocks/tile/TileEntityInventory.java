package lib.enderwizards.sandstone.blocks.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public abstract class TileEntityInventory extends TileEntityBase implements IInventory {

    public ItemStack[] inventory;

    public TileEntityInventory(int size) {
        this.inventory = new ItemStack[size];
    }


    public void setInventory(ItemStack[] inventory) {
        if (inventory != null) this.inventory = inventory;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int size) {
        if (this.inventory[slot] != null) {
            ItemStack stack;

            if (this.inventory[slot].stackSize <= size) {
                stack = this.inventory[slot];
                this.inventory[slot] = null;
                return stack;
            } else {
                stack = this.inventory[slot].splitStack(size);

                if (this.inventory[slot].stackSize == 0) {
                    this.inventory[slot] = null;
                }

                return stack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventory[slot] != null) {
            ItemStack itemstack = this.inventory[slot];
            this.inventory[slot] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public abstract String getInventoryName();

    @Override
    public abstract boolean hasCustomInventoryName();

    @Override
    public abstract int getInventoryStackLimit();

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public abstract void openInventory();

    @Override
    public abstract void closeInventory();

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

}
