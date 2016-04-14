package xreliquary.items.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class FilteredItemStackHandler extends ItemStackHandler {
	public static final int SLOTS_PER_TYPE = 2;
	private ItemStack[] itemStacks;
	private int[] totalAmounts;
	private int[] totalLimits;
	private int[] unitWorth;

	public FilteredItemStackHandler(int[] totalLimits, Item[] items, int[] unitWorth) {
		this(totalLimits, getItemStacks(items), unitWorth);
	}

	public FilteredItemStackHandler(int[] totalLimits, ItemStack[] itemStacks, int[] unitWorth) {
		super(itemStacks.length * SLOTS_PER_TYPE);

		this.totalLimits = totalLimits;
		this.itemStacks = itemStacks;
		this.totalAmounts = new int[this.itemStacks.length];
		this.unitWorth = unitWorth;
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
		return !((slot + 1) % SLOTS_PER_TYPE == 0);
	}

	public int getTotalAmount(int parentSlot) {
		return totalAmounts[parentSlot];
	}

	public void setTotalAmount(int parentSlot, int amount) {
		totalAmounts[parentSlot] = amount;

		updateInputOutputSlots(parentSlot);
	}

	public void markDirty() {
		for(int i = 0; i < totalAmounts.length; i++) {
			int totalAmount = worthToUnits(totalAmounts[i], i);

			int inputSlot = getInputSlot(i);
			int outputSlot = getOutputSlot(i);

			int inputCount = stacks[inputSlot] == null ? 0 : stacks[inputSlot].stackSize;
			int remaining = worthToUnits(totalLimits[i] - totalAmounts[i], i);
			int outputCount = stacks[outputSlot] == null ? 0 : stacks[outputSlot].stackSize;

			if(inputCount != Math.max(itemStacks[i].getMaxStackSize() - remaining, 0))
				totalAmounts[i] += unitsToWorth(inputCount - Math.max(itemStacks[i].getMaxStackSize() - remaining, 0), i);
			if(outputCount != Math.min(totalAmount, itemStacks[i].getMaxStackSize()))
				totalAmounts[i] += unitsToWorth(outputCount - Math.min(totalAmount, itemStacks[i].getMaxStackSize()), i);

			updateInputOutputSlots(i);
		}
	}

	private int worthToUnits(int worth, int parentSlot) {
		return worth / unitWorth[parentSlot];
	}

	private int unitsToWorth(int units, int parentSlot) {
		return units * unitWorth[parentSlot];
	}


	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if(ItemStack.areItemStacksEqual(this.stacks[slot], stack))
			return;

		totalAmounts[getParentSlot(slot)] += unitsToWorth((stack == null ? 0 : stack.stackSize) - stacks[slot].stackSize, getParentSlot(slot));

		if(!isInputSlot(slot))
			this.stacks[slot] = stack;

		updateInputOutputSlots(getParentSlot(slot));
		onContentsChanged(slot);
	}

	private void updateInputOutputSlots(int parentSlot) {
		int outputSlot = getOutputSlot(parentSlot);
		int inputSlot = getInputSlot(parentSlot);

		if(stacks[outputSlot] == null)
			stacks[outputSlot] = itemStacks[parentSlot];

		ItemStack outputStack = stacks[outputSlot];

		if((outputStack.stackSize < outputStack.getMaxStackSize() && outputStack.stackSize < worthToUnits(totalAmounts[parentSlot], parentSlot)) || outputStack.stackSize > worthToUnits(totalAmounts[parentSlot], parentSlot)) {
			outputStack.stackSize = Math.min(outputStack.getMaxStackSize(), worthToUnits(totalAmounts[parentSlot], parentSlot));
		}

		int remainingCapacity = worthToUnits(totalLimits[parentSlot] - totalAmounts[parentSlot], parentSlot);

		int inputAmount = stacks[inputSlot] == null ? 0 : stacks[inputSlot].stackSize;

		if(inputAmount != Math.max(itemStacks[parentSlot].getMaxStackSize() - remainingCapacity, 0)) {
			stacks[inputSlot] = itemStacks[parentSlot];
			stacks[inputSlot].stackSize = Math.max(itemStacks[parentSlot].getMaxStackSize() - remainingCapacity, 0);
		}

		if(stacks[inputSlot] != null && stacks[inputSlot].stackSize == 0) {
			stacks[inputSlot] = null;
		}
	}

	private int getInputSlot(int parentSlot) {
		return (parentSlot * SLOTS_PER_TYPE);
	}

	private int getOutputSlot(int parentSlot) {
		return (parentSlot * SLOTS_PER_TYPE) + 1;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

		if(stack == null || stack.stackSize == 0)
			return null;

		validateSlotIndex(slot);

		int parentSlot = getParentSlot(slot);

		if(!ItemHandlerHelper.canItemStacksStack(stack, itemStacks[parentSlot]))
			return stack;

		ItemStack existing = this.stacks[slot];

		int limit = getStackLimit(slot, stack);
		if(existing != null) {
			limit -= existing.stackSize;
		}

		int remainingTotal = worthToUnits(totalLimits[parentSlot] - totalAmounts[parentSlot], parentSlot);

		limit = Math.min(limit, remainingTotal);

		if(limit <= 0)
			return stack;

		boolean reachedLimit = stack.stackSize > limit;

		if(!simulate) {
			totalAmounts[parentSlot] += unitsToWorth(reachedLimit ? limit : stack.stackSize, parentSlot);
			if(!isInputSlot(slot)) {
				if(existing == null) {
					this.stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
				} else {
					existing.stackSize += reachedLimit ? limit : stack.stackSize;
				}
			}
			updateInputOutputSlots(parentSlot);

			onContentsChanged(slot);
		}

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
					updateInputOutputSlots(getParentSlot(slot));
					return existing;
				} else {
					if(!simulate) {
						this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - amount);
						this.onContentsChanged(slot);
					}

					updateInputOutputSlots(getParentSlot(slot));
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
