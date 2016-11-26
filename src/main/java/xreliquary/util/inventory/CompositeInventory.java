package xreliquary.util.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO finish
class CompositeInventory implements IInventory {
	private List<IInventory> childInventories = new ArrayList<>();

	@Override
	public int getSizeInventory() {
		int sizeInventory = 0;
		for(IInventory inventory : childInventories) {
			sizeInventory += inventory.getSizeInventory();
		}
		return sizeInventory;
	}

	@Override
	public boolean isEmpty() {
		//TODO implement
		return false;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int index) {
		Iterator<IInventory> iterator = childInventories.iterator();

		int currentMax = 0;
		IInventory inventory = null;

		while(iterator.hasNext()) {
			inventory = iterator.next();
			if(index < currentMax + inventory.getSizeInventory())
				break;
			currentMax += inventory.getSizeInventory();
		}

		if(inventory != null)
			return inventory.getStackInSlot(index - currentMax);

		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {

	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {

	}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Nonnull
	@Override
	public String getName() {
		return "";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getName());
	}
}
