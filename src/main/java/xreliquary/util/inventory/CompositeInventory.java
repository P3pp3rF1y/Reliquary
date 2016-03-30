package xreliquary.util.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//TODO finish
public class CompositeInventory implements IInventory {
    List<IInventory> childInventories = new ArrayList<>();

    @Override
    public int getSizeInventory() {
        int sizeInventory = 0;
        for(IInventory inventory : childInventories) {
            sizeInventory += inventory.getSizeInventory();
        }
        return sizeInventory;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        Iterator<IInventory> iterator = childInventories.iterator();

        int currentMax = 0;
        IInventory inventory = null;

        while(iterator.hasNext()) {
            inventory = iterator.next();
            if (index < currentMax + inventory.getSizeInventory())
                break;
            currentMax += inventory.getSizeInventory();
        }

        if (inventory != null)
            return inventory.getStackInSlot(index - currentMax);

        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
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

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return null;
    }
}
