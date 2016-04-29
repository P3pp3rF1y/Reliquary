package xreliquary.items.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilteredItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
	public static final int SLOTS_PER_TYPE = 2;
	private ItemStack[] filterStacks;
	private int[] totalAmounts;
	private int[] totalLimits;
	private int[] unitWorth;
	private boolean dynamicSize = false;

	protected ItemStack[] stacks;

	protected FilteredItemStackHandler(int initialSlots) {
		stacks = new ItemStack[initialSlots * SLOTS_PER_TYPE];
		this.totalAmounts = new int[initialSlots];
	}

	public FilteredItemStackHandler(int[] totalLimits, Item[] items, int[] unitWorth) {
		this(totalLimits, getItemStacks(items), unitWorth);
	}

	public FilteredItemStackHandler(int[] totalLimits, ItemStack[] filterStacks, int[] unitWorth) {
		this(filterStacks.length);

		this.totalLimits = totalLimits;
		this.filterStacks = filterStacks;
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
		if(dynamicSize && amount == 0 && getParentSlotRemovable(parentSlot)) {
			removeValidItemStackFromSlot(parentSlot);
		} else {
			totalAmounts[parentSlot] = amount;

			updateInputOutputSlots(parentSlot);
		}
	}

	private void addValidItemStack(ItemStack stack) {
		expandStacks();

		ItemStack[] expandedStacks = new ItemStack[filterStacks.length + 1];
		System.arraycopy(filterStacks, 0, expandedStacks, 0, filterStacks.length);
		expandedStacks[filterStacks.length] = stack;

		this.filterStacks = expandedStacks;

		this.totalAmounts = expandIntArray(this.totalAmounts, 0);

	}

	private int[] expandIntArray(int[] values, int def) {
		int[] expandedArray = new int[values.length + 1];

		System.arraycopy(values, 0, expandedArray, 0, values.length);
		expandedArray[values.length] = def;

		return expandedArray;
	}

	private int[] removeFromIntArray(int[] values, int index) {
		int[] shrunkArray = new int[values.length - 1];
		if(index > 0) {
			System.arraycopy(values, 0, shrunkArray, 0, index);
		}
		if(index < (values.length - 1)) {
			System.arraycopy(values, index + 1, shrunkArray, index, values.length - (index + 1));
		}

		return shrunkArray;
	}

	private void expandStacks(int size) {
		ItemStack[] expandedStacks = new ItemStack[size];

		if(stacks.length > 0) {
			System.arraycopy(stacks, 0, expandedStacks, 0, stacks.length);
		}

		stacks = expandedStacks;
	}

	private void expandStacks() {
		if(dynamicSize) {
			ItemStack[] expandedStacks = new ItemStack[stacks.length + SLOTS_PER_TYPE];
			System.arraycopy(stacks, 0, expandedStacks, 0, stacks.length);

			stacks = expandedStacks;
		}
	}

	private void removeStack(int parentSlot) {
		if(dynamicSize) {
			ItemStack[] shrunkStacks = new ItemStack[stacks.length - SLOTS_PER_TYPE];
			if(parentSlot > 0) {
				System.arraycopy(stacks, 0, shrunkStacks, 0, parentSlot * SLOTS_PER_TYPE);
			}
			if((parentSlot * SLOTS_PER_TYPE) < (stacks.length - SLOTS_PER_TYPE)) {
				System.arraycopy(stacks, (parentSlot + 1) * SLOTS_PER_TYPE, shrunkStacks, parentSlot * SLOTS_PER_TYPE, stacks.length - ((parentSlot + 1) * SLOTS_PER_TYPE));
			}

			stacks = shrunkStacks;
		}
	}

	private void removeValidItemStackFromSlot(int parentSlot) {
		ItemStack[] shrunkStacks = new ItemStack[filterStacks.length - 1];
		if(parentSlot > 0) {
			System.arraycopy(filterStacks, 0, shrunkStacks, 0, parentSlot);
		}
		if(parentSlot < (filterStacks.length - 1)) {
			System.arraycopy(filterStacks, parentSlot + 1, shrunkStacks, parentSlot, filterStacks.length - (parentSlot + 1));
		}

		this.filterStacks = shrunkStacks;
		this.totalAmounts = removeFromIntArray(this.totalAmounts, parentSlot);

		removeStack(parentSlot);
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

			if(getParentSlotRemovable(i) && totalAmounts[i] == 0) {
				removeValidItemStackFromSlot(i);
			}
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

	protected boolean getParentSlotRemovable(int parentSlot) {
		return false;
	}

	private int unitsToWorth(int units, int parentSlot) {
		return units * getParentSlotUnitWorth(parentSlot);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if(ItemStack.areItemStacksEqual(this.stacks[slot], stack) || (stack != null && (!isItemStackValidForParentSlot(stack, getParentSlot(slot)) || alreadyExistsInAnotherSlot(stack, getParentSlot(slot)))))
			return;

		int parentSlot = getParentSlot(slot);

		if(stack != null && parentSlot == filterStacks.length)
			addValidItemStack(stack);

		totalAmounts[parentSlot] += unitsToWorth((stack == null ? 0 : stack.stackSize) - ((stacks[slot] == null) ? 0 : stacks[slot].stackSize), parentSlot);

		if(totalAmounts[parentSlot] == 0) {
			removeValidItemStackFromSlot(parentSlot);
		} else {
			if(!isInputSlot(slot))
				this.stacks[slot] = stack;

			updateInputOutputSlots(parentSlot);
		}
	}

	private boolean alreadyExistsInAnotherSlot(ItemStack stack, int parentSlot) {
		for(int i = 0; i < filterStacks.length; i++) {
			if(i != parentSlot) {
				if(ItemHandlerHelper.canItemStacksStack(stacks[getOutputSlot(i)], stack))
					return true;
			}
		}

		return false;
	}

	@Override
	public int getSlots() {
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return this.stacks[slot];
	}

	private void updateInputOutputSlots(int parentSlot) {
		ItemStack parentSlotStack = getParentSlotStack(parentSlot);

		//we must be on client so let's just skip the rest
		if (parentSlotStack == null)
			return;

		int outputSlot = getOutputSlot(parentSlot);
		int inputSlot = getInputSlot(parentSlot);

		if(stacks[outputSlot] == null)
			stacks[outputSlot] = parentSlotStack.copy();

		ItemStack outputStack = stacks[outputSlot];

		if((outputStack.stackSize < parentSlotStack.getMaxStackSize() && outputStack.stackSize < worthToUnits(totalAmounts[parentSlot], parentSlot)) || outputStack.stackSize > worthToUnits(totalAmounts[parentSlot], parentSlot)) {
			outputStack.stackSize = Math.min(parentSlotStack.getMaxStackSize(), worthToUnits(totalAmounts[parentSlot], parentSlot));
		}

		int remainingCapacity = worthToUnits(getParentSlotLimit(parentSlot) - totalAmounts[parentSlot], parentSlot);

		int inputAmount = stacks[inputSlot] == null ? 0 : stacks[inputSlot].stackSize;

		if(inputAmount != Math.max(parentSlotStack.getMaxStackSize() - remainingCapacity, 0)) {
			stacks[inputSlot] = parentSlotStack.copy();
			stacks[inputSlot].stackSize = Math.max(parentSlotStack.getMaxStackSize() - remainingCapacity, 0);
		}

		if(stacks[inputSlot] != null && stacks[inputSlot].stackSize == 0) {
			stacks[inputSlot] = null;
		}
	}

	protected ItemStack getParentSlotStack(int parentSlot) {
		return filterStacks[parentSlot];
	}

	protected boolean isItemStackValidForParentSlot(ItemStack stack, int parentSlot) {
		return ItemHandlerHelper.canItemStacksStack(stack, filterStacks[parentSlot]);
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

		if(!isItemStackValidForParentSlot(stack, parentSlot) || alreadyExistsInAnotherSlot(stack, parentSlot))
			return stack;

		ItemStack existing = this.stacks[slot];

		int limit = getStackLimit(slot, parentSlot >= filterStacks.length ? stack : filterStacks[parentSlot]);
		if(existing != null) {
			limit -= existing.stackSize;
		}

		int remainingTotal = worthToUnits(getParentSlotLimit(parentSlot) - (parentSlot >= totalAmounts.length ? 0 : totalAmounts[parentSlot]), parentSlot);

		limit = Math.min(limit, remainingTotal);

		if(limit <= 0)
			return stack;

		boolean reachedLimit = stack.stackSize > limit;

		if(!simulate) {
			if(parentSlot == filterStacks.length)
				addValidItemStack(stack);

			totalAmounts[parentSlot] += unitsToWorth(reachedLimit ? limit : stack.stackSize, parentSlot);
			if(!isInputSlot(slot)) {
				if(existing == null) {
					this.stacks[slot] = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
				} else {
					existing.stackSize += reachedLimit ? limit : stack.stackSize;
				}
			}
			updateInputOutputSlots(parentSlot);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
	}

	private boolean filterStackExists(ItemStack stack) {
		for(ItemStack filterStack : filterStacks) {
			if(ItemHandlerHelper.canItemStacksStack(filterStack, stack))
				return true;
		}

		return false;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(amount == 0) {
			return null;
		} else {
			this.validateSlotIndex(slot);

			if(this.stacks[slot] == null)
				return null;

			ItemStack existing = this.stacks[slot];
			if(existing == null) {
				return null;
			} else {
				int numberRemoved = Math.min(existing.stackSize, amount);
				if(!simulate) {
					totalAmounts[getParentSlot(slot)] -= numberRemoved;
					this.stacks[slot] = ItemHandlerHelper.copyStackWithSize(existing, existing.stackSize - numberRemoved);

					if(totalAmounts[getParentSlot(slot)] == 0 && dynamicSize) {
						removeValidItemStackFromSlot(getParentSlot(slot));
					} else {
						updateInputOutputSlots(getParentSlot(slot));
					}

				}
				return ItemHandlerHelper.copyStackWithSize(existing, numberRemoved);
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i] != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				stacks[i].writeToNBT(itemTag);
				nbtTagList.appendTag(itemTag);
			}
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		nbt.setInteger("Size", stacks.length);

		NBTTagList nbtAmountsList = new NBTTagList();

		for(int i = 0; i < totalAmounts.length; i++) {
			NBTTagInt amountTag = new NBTTagInt(totalAmounts[i]);
			nbtAmountsList.appendTag(amountTag);
		}

		nbt.setTag("TotalAmounts", nbtAmountsList);

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagList amounts = nbt.getTagList("TotalAmounts", 3);

		for(int i = 0; i < totalAmounts.length; i++) {
			totalAmounts[i] = ((NBTTagInt) amounts.get(i)).getInt();
		}

		int stacksSize = nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.length;
		setSize(stacksSize);
		setFilterStacksSize((stacksSize - (dynamicSize ? SLOTS_PER_TYPE : 0)) / SLOTS_PER_TYPE);

		NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");

			if(slot >= 0 && slot < stacks.length) {
				stacks[slot] = ItemStack.loadItemStackFromNBT(itemTags);

				ItemStack filterStack = stacks[slot].copy();
				filterStack.stackSize = 1;
				filterStacks[getParentSlot(slot)] = filterStack;
			}
		}
	}

	public void setDynamicSize(boolean dynamicSize) {
		if(this.dynamicSize != dynamicSize) {
			this.dynamicSize = dynamicSize;

			if(this.dynamicSize) {
				expandStacks();
			} else {
				removeStack(this.filterStacks.length);
			}
		}
	}

	protected ItemStack[] getFilterStacks() {
		return filterStacks;
	}

	protected void validateSlotIndex(int slot) {
		if(slot < 0 || slot >= stacks.length)
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.length + ")");
	}

	protected int getStackLimit(int slot, ItemStack stack) {
		return stack.getMaxStackSize();
	}

	private void setSize(int size) {
		stacks = new ItemStack[size];
	}

	private void setFilterStacksSize(int size) {
		if (filterStacks.length != size) {
			ItemStack[] expandedFilterStacks = new ItemStack[size];
			System.arraycopy(filterStacks, 0, expandedFilterStacks, 0, filterStacks.length);

			filterStacks = expandedFilterStacks;
		}
	}
}
