package xreliquary.items.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class FilteredItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
	public static final int SLOTS_PER_TYPE = 2;
	//TODO refactor all these collections / arrays to a single collection of classes that encapsulate individual filtered items
	NonNullList<ItemStack> filterStacks;
	private int[] totalAmounts;
	private int[] totalLimits;
	private int[] unitWorth;
	private boolean dynamicSize = false;

	private NonNullList<ItemStack> stacks;

	protected FilteredItemStackHandler(int initialSlots) {
		stacks = NonNullList.withSize(initialSlots * SLOTS_PER_TYPE, ItemStack.EMPTY);
		this.totalAmounts = new int[initialSlots];
		this.filterStacks = NonNullList.withSize(initialSlots, ItemStack.EMPTY);
	}

	public FilteredItemStackHandler(int[] totalLimits, Item[] items, int[] unitWorth) {
		this(totalLimits, getItemStacks(items), unitWorth);
	}

	public FilteredItemStackHandler(int[] totalLimits, NonNullList<ItemStack> filterStacks, int[] unitWorth) {
		this(filterStacks.size());

		this.totalLimits = totalLimits;
		this.filterStacks = filterStacks;
		this.unitWorth = unitWorth;
	}

	private static NonNullList<ItemStack> getItemStacks(Item[] items) {
		NonNullList<ItemStack> itemStacks = NonNullList.create();
		for(Item item : items) {
			itemStacks.add(new ItemStack(item));
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
		return parentSlot < totalAmounts.length ? totalAmounts[parentSlot] : 0;
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

		this.filterStacks.add(stack);

		this.totalAmounts = expandIntArray(this.totalAmounts, 0);
	}

	private int[] expandIntArray(int[] values, int newSize, int def) {
		int[] expandedArray = new int[newSize];

		System.arraycopy(values, 0, expandedArray, 0, values.length);

		return expandedArray;
	}

	private int[] expandIntArray(int[] values, int def) {
		int[] expandedArray = expandIntArray(values, values.length + 1, def);
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

	private void expandStacks() {
		if(dynamicSize) {
			for (int i=0; i<SLOTS_PER_TYPE; i++) {
				stacks.add(ItemStack.EMPTY);
			}
		}
	}

	private void removeStack(int parentSlot) {
		if(dynamicSize) {
			for (int i=0; i < SLOTS_PER_TYPE; i++) {
				stacks.remove(parentSlot * SLOTS_PER_TYPE);
			}
		}
	}

	private void removeValidItemStackFromSlot(int parentSlot) {
		this.filterStacks.remove(parentSlot);
		this.totalAmounts = removeFromIntArray(this.totalAmounts, parentSlot);

		removeStack(parentSlot);
	}

	public void markDirty() {
		for(int i = 0; i < totalAmounts.length; i++) {
			int totalAmount = worthToUnits(totalAmounts[i], i);

			int inputSlot = getInputSlot(i);
			int outputSlot = getOutputSlot(i);

			int inputCount = stacks.get(inputSlot).isEmpty() ? 0 : stacks.get(inputSlot).getCount();
			int remaining = worthToUnits(getParentSlotLimit(i) - totalAmounts[i], i);
			int outputCount = stacks.get(outputSlot).isEmpty() ? 0 : stacks.get(outputSlot).getCount();

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
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		if(ItemStack.areItemStacksEqual(this.stacks.get(slot), stack) || (!stack.isEmpty() && (!isItemStackValidForParentSlot(stack, getParentSlot(slot)) || alreadyExistsInAnotherSlot(stack, getParentSlot(slot)))))
			return;

		int parentSlot = getParentSlot(slot);

		if(!stack.isEmpty() && parentSlot == filterStacks.size())
			addValidItemStack(stack);

		totalAmounts[parentSlot] += unitsToWorth((stack.isEmpty() ? 0 : stack.getCount()) - (stacks.get(slot).isEmpty() ? 0 : stacks.get(slot).getCount()), parentSlot);

		if(totalAmounts[parentSlot] == 0) {
			removeValidItemStackFromSlot(parentSlot);
		} else {
			if(!isInputSlot(slot))
				this.stacks.set(slot, stack);

			updateInputOutputSlots(parentSlot);
		}
	}

	private boolean alreadyExistsInAnotherSlot(ItemStack stack, int parentSlot) {
		for(int i = 0; i < filterStacks.size(); i++) {
			if(i != parentSlot) {
				if(ItemHandlerHelper.canItemStacksStack(stacks.get(getOutputSlot(i)), stack))
					return true;
			}
		}

		return false;
	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return this.stacks.get(slot);
	}

	private void updateInputOutputSlots(int parentSlot) {
		ItemStack parentSlotStack = getParentSlotStack(parentSlot);

		//we must be on client so let's just skip the rest
		if(parentSlotStack.isEmpty())
			return;

		int outputSlot = getOutputSlot(parentSlot);
		int inputSlot = getInputSlot(parentSlot);

		if(stacks.get(outputSlot).isEmpty())
			stacks.set(outputSlot, parentSlotStack.copy());

		ItemStack outputStack = stacks.get(outputSlot);

		if((outputStack.getCount() < parentSlotStack.getMaxStackSize() && outputStack.getCount() < worthToUnits(totalAmounts[parentSlot], parentSlot)) || outputStack.getCount() > worthToUnits(totalAmounts[parentSlot], parentSlot)) {
			outputStack.setCount(Math.min(parentSlotStack.getMaxStackSize(), worthToUnits(totalAmounts[parentSlot], parentSlot)));
		}

		int remainingCapacity = worthToUnits(getParentSlotLimit(parentSlot) - totalAmounts[parentSlot], parentSlot);

		int inputAmount = stacks.get(inputSlot).isEmpty() ? 0 : stacks.get(inputSlot).getCount();

		if(inputAmount != Math.max(parentSlotStack.getMaxStackSize() - remainingCapacity, 0)) {
			stacks.set(inputSlot, parentSlotStack.copy());
			stacks.get(inputSlot).setCount(Math.max(parentSlotStack.getMaxStackSize() - remainingCapacity, 0));
		}

		if(!stacks.get(inputSlot).isEmpty() && stacks.get(inputSlot).getCount() == 0) {
			stacks.set(inputSlot, ItemStack.EMPTY);
		}
	}

	private ItemStack getParentSlotStack(int parentSlot) {
		return filterStacks.get(parentSlot);
	}

	public void setParentSlotStack(int parentSlot, ItemStack filterStack) {
		if(filterStacks.get(parentSlot).isEmpty()) {
			filterStacks.set(parentSlot, filterStack.copy());
		}
	}

	protected boolean isItemStackValidForParentSlot(ItemStack stack, int parentSlot) {
		return ItemHandlerHelper.canItemStacksStack(stack, filterStacks.get(parentSlot));
	}

	private int getInputSlot(int parentSlot) {
		return (parentSlot * SLOTS_PER_TYPE);
	}

	private int getOutputSlot(int parentSlot) {
		return (parentSlot * SLOTS_PER_TYPE) + 1;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

		if(stack.isEmpty() || stack.getCount() == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		int parentSlot = getParentSlot(slot);

		if(!isItemStackValidForParentSlot(stack, parentSlot) || alreadyExistsInAnotherSlot(stack, parentSlot))
			return stack;

		ItemStack existing = this.stacks.get(slot);

		int limit = getStackLimit(parentSlot >= filterStacks.size() ? stack : filterStacks.get(parentSlot));
		if(!existing.isEmpty()) {
			limit -= existing.getCount();
		}

		int remainingTotal = worthToUnits(getParentSlotLimit(parentSlot) - (parentSlot >= totalAmounts.length ? 0 : totalAmounts[parentSlot]), parentSlot);

		limit = Math.min(limit, remainingTotal);

		if(limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if(!simulate) {
			if(parentSlot == filterStacks.size())
				addValidItemStack(stack);

			totalAmounts[parentSlot] += unitsToWorth(reachedLimit ? limit : stack.getCount(), parentSlot);
			if(!isInputSlot(slot)) {
				if(existing.isEmpty()) {
					this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
				} else {
					existing.grow(reachedLimit ? limit : stack.getCount());
				}
			}
			updateInputOutputSlots(parentSlot);
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Nonnull
	public ItemStack getStackInParentSlot(int parentSlot) {
		if(parentSlot >= filterStacks.size())
			return ItemStack.EMPTY;

		return filterStacks.get(parentSlot);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(amount == 0) {
			return ItemStack.EMPTY;
		} else {
			this.validateSlotIndex(slot);

			if(this.stacks.get(slot).isEmpty())
				return ItemStack.EMPTY;

			ItemStack existing = this.stacks.get(slot);
			if(existing.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				int numberRemoved = Math.min(existing.getCount(), amount);
				if(!simulate) {
					totalAmounts[getParentSlot(slot)] -= numberRemoved;
					this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - numberRemoved));

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
		for(int i = 0; i < stacks.size(); i++) {
			if(!stacks.get(i).isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				stacks.get(i).writeToNBT(itemTag);
				nbtTagList.appendTag(itemTag);
			}
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Items", nbtTagList);
		nbt.setInteger("Size", stacks.size());

		NBTTagList nbtAmountsList = new NBTTagList();

		for(int totalAmount : totalAmounts) {
			NBTTagInt amountTag = new NBTTagInt(totalAmount);
			nbtAmountsList.appendTag(amountTag);
		}

		nbt.setTag("TotalAmounts", nbtAmountsList);

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int stacksSize = nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size();
		setSize(stacksSize);

		NBTTagList amounts = nbt.getTagList("TotalAmounts", 3);
		setFilterStacksSize(amounts.tagCount());

		for(int i = 0; i < amounts.tagCount(); i++) {
			totalAmounts[i] = ((NBTTagInt) amounts.get(i)).getInt();
		}

		NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
			int slot = itemTags.getInteger("Slot");

			if(slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, new ItemStack(itemTags));

				if(filterStacks.size() > getParentSlot(slot)) {
					ItemStack filterStack = stacks.get(slot).copy();
					filterStack.setCount(1);
					filterStacks.set(getParentSlot(slot), filterStack);
				}
			}
		}
	}

	void setDynamicSize(boolean dynamicSize) {
		if(this.dynamicSize != dynamicSize) {
			this.dynamicSize = dynamicSize;

			if(this.dynamicSize) {
				expandStacks();
			} else {
				removeStack(this.filterStacks.size());
			}
		}
	}

	NonNullList<ItemStack> getFilterStacks() {
		return filterStacks;
	}

	private void validateSlotIndex(int slot) {
		if(slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	private int getStackLimit(ItemStack stack) {
		return stack.getMaxStackSize();
	}

	private void setSize(int size) {
		if(stacks.size() != size) {
			stacks = NonNullList.withSize(size, ItemStack.EMPTY);
		}
	}

	private void setFilterStacksSize(int size) {
		if(filterStacks.size() != size) {
			filterStacks = NonNullList.withSize(size, ItemStack.EMPTY);

			totalAmounts = new int[size];
		}
	}
}
