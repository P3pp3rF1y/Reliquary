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

	protected FilteredItemStackHandler(int initialSlots) {
		super(initialSlots * SLOTS_PER_TYPE);
		this.totalAmounts = new int[initialSlots];
	}

	public FilteredItemStackHandler(int[] totalLimits, Item[] items, int[] unitWorth) {
		this(totalLimits, getItemStacks(items), unitWorth);
	}

	public FilteredItemStackHandler(int[] totalLimits, ItemStack[] itemStacks, int[] unitWorth) {
		this(itemStacks.length);

		this.totalLimits = totalLimits;
		this.itemStacks = itemStacks;
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
			int remaining = worthToUnits(getParentSlotLimit(i) - totalAmounts[i], i);
			int outputCount = stacks[outputSlot] == null ? 0 : stacks[outputSlot].stackSize;

			ItemStack parentSlotStack = getParentSlotStack(i);

			if(inputCount != Math.max(parentSlotStack.getMaxStackSize() - remaining, 0))
				totalAmounts[i] += unitsToWorth(inputCount - Math.max(parentSlotStack.getMaxStackSize() - remaining, 0), i);
			if(outputCount != Math.min(totalAmount, parentSlotStack.getMaxStackSize()))
				totalAmounts[i] += unitsToWorth(outputCount - Math.min(totalAmount, parentSlotStack.getMaxStackSize()), i);

			updateInputOutputSlots(i);
		}
	}

	protected int getParentSlotLimit(int parentSlot) {
		return totalLimits[parentSlot];
	}

	private int worthToUnits(int worth, int parentSlot) {
		return worth / getParentSlotUnitWorth(parentSlot);
	}

	protected int getParentSlotUnitWorth(int parentSlot) {
		return unitWorth[parentSlot];
	}

	protected void addValidItemStack(ItemStack stack) {
		ItemStack[] expandedStacks = new ItemStack[itemStacks.length + 1];
		System.arraycopy(itemStacks, 0, expandedStacks, 0, itemStacks.length);
		expandedStacks[itemStacks.length] = stack;

		this.itemStacks = expandedStacks;
	}

	protected void removeValidItemStackFromSlot(int parentSlot) {
		ItemStack[] shrunkStacks = new ItemStack[itemStacks.length - 1];
		if(parentSlot > 0) {
			System.arraycopy(itemStacks, 0, shrunkStacks, 0, parentSlot);
		}
		if(parentSlot < (itemStacks.length - 1)) {
			System.arraycopy(itemStacks, parentSlot + 1, shrunkStacks, parentSlot, itemStacks.length - (parentSlot + 1));
		}

		this.itemStacks = shrunkStacks;
	}

	private int unitsToWorth(int units, int parentSlot) {
		return units * getParentSlotUnitWorth(parentSlot);
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
			stacks[outputSlot] = getParentSlotStack(parentSlot);

		ItemStack outputStack = stacks[outputSlot];

		if((outputStack.stackSize < outputStack.getMaxStackSize() && outputStack.stackSize < worthToUnits(totalAmounts[parentSlot], parentSlot)) || outputStack.stackSize > worthToUnits(totalAmounts[parentSlot], parentSlot)) {
			outputStack.stackSize = Math.min(outputStack.getMaxStackSize(), worthToUnits(totalAmounts[parentSlot], parentSlot));
		}

		int remainingCapacity = worthToUnits(getParentSlotLimit(parentSlot) - totalAmounts[parentSlot], parentSlot);

		int inputAmount = stacks[inputSlot] == null ? 0 : stacks[inputSlot].stackSize;

		ItemStack parentSlotStack = getParentSlotStack(parentSlot);

		if(inputAmount != Math.max(parentSlotStack.getMaxStackSize() - remainingCapacity, 0)) {
			stacks[inputSlot] = parentSlotStack;
			stacks[inputSlot].stackSize = Math.max(parentSlotStack.getMaxStackSize() - remainingCapacity, 0);
		}

		if(stacks[inputSlot] != null && stacks[inputSlot].stackSize == 0) {
			stacks[inputSlot] = null;
		}
	}

	protected ItemStack getParentSlotStack(int parentSlot) {
		return itemStacks[parentSlot];
	}

	protected boolean isItemStackValidForParentSlot(ItemStack stack, int parentSlot) {
		return ItemHandlerHelper.canItemStacksStack(stack, itemStacks[parentSlot]);
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

		if(!isItemStackValidForParentSlot(stack, parentSlot))
			return stack;

		ItemStack existing = this.stacks[slot];

		int limit = getStackLimit(slot, stack);
		if(existing != null) {
			limit -= existing.stackSize;
		}

		int remainingTotal = worthToUnits(getParentSlotLimit(parentSlot) - totalAmounts[parentSlot], parentSlot);

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
