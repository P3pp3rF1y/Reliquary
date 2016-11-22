package xreliquary.blocks.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

abstract class TileEntityInventory extends TileEntityBase implements IInventory {

	public NonNullList<ItemStack> inventory;

	TileEntityInventory(int size) {
		this.inventory = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if(!this.inventory.get(slot).isEmpty()) {
			return this.inventory.get(slot).splitStack(size);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return ItemStackHelper.getAndRemove(this.inventory, slot);
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		this.inventory.set(slot, stack);
		if(stack != ItemStack.EMPTY && stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public abstract int getInventoryStackLimit();

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return true;
	}

	@Override
	public abstract void openInventory(@Nonnull EntityPlayer player);

	@Override
	public abstract void closeInventory(@Nonnull EntityPlayer player);

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, ItemStack.EMPTY);
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack stack : inventory) {
			if(!stack.isEmpty())
				return false;
		}
		return true;
	}
}

