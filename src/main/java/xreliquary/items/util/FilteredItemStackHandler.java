package xreliquary.items.util;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class FilteredItemStackHandler extends ItemStackHandler {
	private Item[] items;
	private int[] limits;

	public FilteredItemStackHandler(int[] limits, Item[] items) {
		super(items.length);
		this.items = items;
		this.limits = limits;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (slot < items.length && stack.getItem() == items[slot])
			super.setStackInSlot(slot, stack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (slot < items.length && stack.getItem() == items[slot])
			return super.insertItem(slot, stack, simulate);

		return null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(amount == 0) {
			return null;
		} else {
			this.validateSlotIndex(slot);
			ItemStack existing = this.stacks[slot];
			if(existing == null) {
				return null;
			} else {
				if(existing.stackSize <= amount) {
					if(!simulate) {
						this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, 0);
						this.onContentsChanged(slot);
					}

					return existing;
				} else {
					if(!simulate) {
						this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - amount);
						this.onContentsChanged(slot);
					}

					return ItemHandlerHelper.copyStackWithSize(existing, amount);
				}
			}
		}
	}

	@Override
	protected int getStackLimit(int slot, ItemStack stack) {
		if (slot < limits.length)
			return limits[slot];
		return 0;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();

		for(int i = 0; i < stacks.length; ++i) {
			if(stacks[i] != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				stacks[i].writeToNBT(itemTag);

				//This is the only reason of this copy paste - big size of item stacks in this item handler
				itemTag.setInteger("IntCount", stacks[i].stackSize);
				nbtTagList.appendTag(itemTag);
			}
		}

		NBTTagCompound var4 = new NBTTagCompound();
		var4.setTag("Items", nbtTagList);
		var4.setInteger("Size", stacks.length);
		return var4;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.setSize(nbt.hasKey("Size", 3)?nbt.getInteger("Size"):this.stacks.length);
		NBTTagList tagList = nbt.getTagList("Items", 10);

		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");
			if(slot >= 0 && slot < this.stacks.length) {
				this.stacks[slot] = ItemStack.loadItemStackFromNBT(itemTags);

				//This is the only reason of this copy paste - big size of item stacks in this item handler
				this.stacks[slot].stackSize = itemTags.getInteger("IntCount");
			}
		}

		this.onLoad();
	}
}
