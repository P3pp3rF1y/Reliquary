package xreliquary.items.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class FilteredItemStackHandler extends ItemStackHandler {
	public static final int MAX_SLOTS_PER_TYPE = 3;
	private ItemStack[] itemStacks;
	private int[] totalAmounts;
	private int[] limits;

	public FilteredItemStackHandler(int[] limits, Item[] items) {
		this(limits, getItemStacks(items));
	}

	public FilteredItemStackHandler(int[] limits, ItemStack[] itemStacks) {
		super(getTotalSlots(itemStacks, limits));

		this.limits = limits;
		this.itemStacks = itemStacks;
		this.totalAmounts = new int[this.itemStacks.length];
	}

	private static int getTotalSlots(ItemStack[] itemStacks, int[] limits) {
		int totalSlots = 0;

		for(int i = 0; i < itemStacks.length; i++) {
			totalSlots = Math.min((limits[i] / itemStacks[i].getMaxStackSize()) + 1, MAX_SLOTS_PER_TYPE);
		}

		return totalSlots;
	}

	private static ItemStack[] getItemStacks(Item[] items) {
		ItemStack[] itemStacks = new ItemStack[items.length];
		for(int i = 0; i < items.length; i++) {
			itemStacks[i] = new ItemStack(items[i]);
		}
		return itemStacks;
	}

	private int getBigSlot(int slot) {
		int currentSlot = 0;
		int adjustedSlot = slot;
		for(int i = 0; i < slotNumbers.length; i++) {
			if(slotNumbers[i] > adjustedSlot)
				break;

			adjustedSlot -= slotNumbers[i];
			currentSlot++;
		}
		return currentSlot;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if(slot < this.stacks.length && ItemHandlerHelper.canItemStacksStack(stack, itemStacks[getBigSlot(slot)]) check limit)
		super.setStackInSlot(slot, stack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(slot < itemStacks.length && stack.getItem() == itemStacks[slot] check limit)
			return super.insertItem(slot, stack, simulate);

		split or null

				keep first two slots empty

		return null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if extracting from the first slot leave item in otherwise null
				needs setting to be able to empty void tear


				keep the last slot filled or with 0 stacksize
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
		get big slot
				get max stack size
		if(slot < limits.length)
			return limits[slot];
		return 0;
	}

	update nbt to store big count only once

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tagCompound = super.serializeNBT();

		NBTTagList nbtTagList = new NBTTagList();


		itemTag.setInteger("IntCount", totalAmounts);


		tagCompound.setTag("TotalAmounts", nbtTagList);

		return tagCompound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		totalAmounts cycle

		super.deserializeNBT(nbt);
	}
}
