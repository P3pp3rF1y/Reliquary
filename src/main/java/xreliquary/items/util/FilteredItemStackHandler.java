package xreliquary.items.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class FilteredItemStackHandler extends ItemStackHandler {
	public static final int SLOTS_PER_TYPE = 3;
	private ItemStack[] itemStacks;
	private int[] totalAmounts;
	private int[] totalLimits;

	public FilteredItemStackHandler(int[] totalLimits, Item[] items) {
		this(totalLimits, getItemStacks(items));
	}

	public FilteredItemStackHandler(int[] totalLimits, ItemStack[] itemStacks) {
		super(itemStacks.length * SLOTS_PER_TYPE);

		this.totalLimits = totalLimits;
		this.itemStacks = itemStacks;
		this.totalAmounts = new int[this.itemStacks.length];
	}

	private static ItemStack[] getItemStacks(Item[] items) {
		ItemStack[] itemStacks = new ItemStack[items.length];
		for(int i = 0; i < items.length; i++) {
			itemStacks[i] = new ItemStack(items[i]);
		}
		return itemStacks;
	}

	private int getParentSlot(int slot) {
		return slot / SLOTS_PER_TYPE;
	}

	private boolean isInputSlot(int slot) {
		return !((slot + 1) % 3 == 0);
	}

	public int getTotalAmount(int parentSlot) {
		return totalAmounts[parentSlot];
	}

	public void setTotalAmount(int parentSlot, int amount) {
		totalAmounts[parentSlot] = amount;

		updateOutputSlot(parentSlot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if(ItemStack.areItemStacksEqual(this.stacks[slot], stack))
			return;

		totalAmounts[getParentSlot(slot)] += ((stack == null ? 0 : stack.stackSize) - stacks[slot].stackSize);

		if(!isInputSlot(slot))
			this.stacks[slot] = stack;

		updateOutputSlot(getParentSlot(slot));
		onContentsChanged(slot);
	}

	private void updateOutputSlot(int parentSlot) {
		int outputSlot = getOutputSlot(parentSlot);

		if(stacks[outputSlot] == null)
			stacks[outputSlot] = itemStacks[parentSlot];

		ItemStack outputStack = stacks[outputSlot];

		if((outputStack.stackSize < outputStack.getMaxStackSize() && outputStack.stackSize < totalAmounts[parentSlot]) || outputStack.stackSize > totalAmounts[parentSlot]) {
			outputStack.stackSize = Math.min(outputStack.getMaxStackSize(), totalAmounts[parentSlot]);
		}
	}

	private int getOutputSlot(int parentSlot) {
		return (parentSlot * SLOTS_PER_TYPE) + 2;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

		if(stack == null || stack.stackSize == 0)
			return null;

		validateSlotIndex(slot);

		if(!ItemHandlerHelper.canItemStacksStack(stack, itemStacks[getParentSlot(slot)]))
			return stack;

		ItemStack existing = this.stacks[slot];

		int limit = getStackLimit(slot, stack);
		if(existing != null) {
			limit -= existing.stackSize;
		}

		int remainingTotal = totalLimits[getParentSlot(slot)] - totalAmounts[getParentSlot(slot)];

		limit = Math.min(limit, remainingTotal);

		if(limit <= 0)
			return stack;

		boolean reachedLimit = stack.stackSize > limit;

		if(!simulate) {
			totalAmounts[getParentSlot(slot)] += reachedLimit ? limit : stack.stackSize;
			if(!isInputSlot(slot)) {
				if(existing == null) {
					this.stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
				} else {
					existing.stackSize += reachedLimit ? limit : stack.stackSize;
				}
			}
			onContentsChanged(slot);
		}

		updateOutputSlot(getParentSlot(slot));

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
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
					updateOutputSlot(getParentSlot(slot));
					return existing;
				} else {
					if(!simulate) {
						this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - amount);
						this.onContentsChanged(slot);
					}

					updateOutputSlot(getParentSlot(slot));
					return ItemHandlerHelper.copyStackWithSize(existing, amount);
				}
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tagCompound = super.serializeNBT();

		NBTTagList nbtTagList = new NBTTagList();

		for(int i = 0; i < totalAmounts.length; i++) {
			NBTTagInt amountTag = new NBTTagInt(totalAmounts[i]);
			nbtTagList.appendTag(amountTag);
		}

		tagCompound.setTag("TotalAmounts", nbtTagList);

		return tagCompound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagList amounts = nbt.getTagList("TotalAmounts", 3);

		for(int i = 0; i < totalAmounts.length; i++) {
			totalAmounts[i] = ((NBTTagInt) amounts.get(i)).getInt();
		}

		super.deserializeNBT(nbt);
	}
}
