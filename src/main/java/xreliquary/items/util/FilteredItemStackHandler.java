package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Iterator;
import java.util.List;

public class FilteredItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundNBT> {
	private boolean dynamicSlotNumber = false;
	private List<RemovableStack> filteredBigItemStacks;
	private static final String NOT_IN_RANGE_ERROR = "%s %d not in valid range - (0, %d)";

	public FilteredItemStackHandler(List<RemovableStack> filteredBigItemStacks) {
		this.filteredBigItemStacks = filteredBigItemStacks;
	}

	public void setTotalAmount(int bigStackSlot, int amount) {
		if (dynamicSlotNumber && amount == 0 && getBigStackRemovable(bigStackSlot)) {
			filteredBigItemStacks.remove(bigStackSlot);
		} else {
			filteredBigItemStacks.get(bigStackSlot).getStack().setAmount(amount);
		}
	}

	public int getTotalAmount(int bigStackSlot) {
		validateBigStackSlot(bigStackSlot);
		return filteredBigItemStacks.get(bigStackSlot).getStack().getAmount();
	}

	private void validateBigStackSlot(int bigStackSlot) {
		if (bigStackSlot < 0 || bigStackSlot >= filteredBigItemStacks.size()) {
			throw new IllegalArgumentException(String.format(NOT_IN_RANGE_ERROR, "Big Stack Slot", bigStackSlot, filteredBigItemStacks.size() - 1));
		}
	}

	public void markDirty() {
		Iterator<RemovableStack> it = filteredBigItemStacks.iterator();

		while (it.hasNext()) {
			RemovableStack removableStack = it.next();
			FilteredBigItemStack stack = removableStack.getStack();
			stack.markDirty();
			if (stack.isEmpty() && removableStack.canRemove) {
				it.remove();
			}
		}
	}

	@SuppressWarnings("squid:S1172")
	protected boolean getBigStackRemovable(int bigStackSlot) {
		return false;
	}

	public FilteredBigItemStack getBigStack(int bigStackSlot) {
		validateBigStackSlot(bigStackSlot);
		return filteredBigItemStacks.get(bigStackSlot).getStack();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		int bigStackSlot = getBigStackSlot(slot);
		if (filteredBigItemStacks.size() > bigStackSlot) {
			if (!isValidForBigStackSlot(stack, bigStackSlot)) {
				return;
			}
		} else if (alreadyExistsInAnotherSlot(stack, bigStackSlot)) {
			return;
		}

		if (isValidForDynamicStack(stack)) {
			addDynamicBigStack(stack, bigStackSlot);
		}

		updateBigStack(slot, stack, bigStackSlot);
	}

	protected boolean isValidForBigStackSlot(ItemStack stack, int bigStackSlot) {
		return filteredBigItemStacks.get(bigStackSlot).getStack().isValid(stack);
	}

	private void updateBigStack(int slot, ItemStack stack, int bigStackSlot) {
		RemovableStack removableStack = filteredBigItemStacks.get(bigStackSlot);
		FilteredBigItemStack bigStack = removableStack.getStack();
		if (isInputSlot(slot)) {
			bigStack.setInputStack(stack);
		} else {
			bigStack.setOutputStack(stack);
		}
		bigStack.markDirty();

		if (bigStack.isEmpty() && removableStack.canRemove()) {
			filteredBigItemStacks.remove(bigStackSlot);
		}
	}

	private void addDynamicBigStack(ItemStack stack, int bigStackSlot) {
		if (dynamicSlotNumber && !stack.isEmpty() && bigStackSlot == filteredBigItemStacks.size()) {
			ItemStack filter = stack.copy();
			filter.setCount(1);
			filteredBigItemStacks.add(new RemovableStack(new FilteredBigItemStack(filter, getDynamicStackLimit()), true));
		}
	}

	protected int getDynamicStackLimit() {
		return Integer.MAX_VALUE;
	}

	protected int getBigStackSlot(int slot) {
		return slot / 2;
	}

	private boolean alreadyExistsInAnotherSlot(ItemStack stack, int bigStackSlot) {
		for (int i = 0; i < filteredBigItemStacks.size(); i++) {
			if (i != bigStackSlot && isValidForBigStackSlot(stack, i)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int getSlots() {
		return filteredBigItemStacks.size() * 2 + (dynamicSlotNumber ? 1 : 0);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	public int getBigStackSlots() {
		return filteredBigItemStacks.size();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		return isValidForBigStackSlot(stack, getBigStackSlot(slot));
	}

	private boolean isInputSlot(int slot) {
		return slot % 2 == 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		int bigStackSlot = getBigStackSlot(slot);
		if (bigStackSlot < filteredBigItemStacks.size()) {
			FilteredBigItemStack bigStack = filteredBigItemStacks.get(0).getStack();
			return isInputSlot(slot) ? bigStack.getInputStack() : bigStack.getOutputStack();
		} else {
			return ItemStack.EMPTY;
		}
	}

	void setBigStack(int bigStackSlot, RemovableStack removableStack) {
		if (bigStackSlot < 0 || bigStackSlot > filteredBigItemStacks.size()) {
			throw new IllegalArgumentException(String.format(NOT_IN_RANGE_ERROR, "Big Stack Slot", bigStackSlot, filteredBigItemStacks.size()));
		} else if (bigStackSlot == filteredBigItemStacks.size()) {
			filteredBigItemStacks.add(removableStack);
			return;
		}
		filteredBigItemStacks.set(bigStackSlot, removableStack);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty() || stack.getCount() == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		int bigStackSlot = getBigStackSlot(slot);

		if (bigStackSlot == filteredBigItemStacks.size()) {
			if (!alreadyExistsInAnotherSlot(stack, bigStackSlot) && isValidForDynamicStack(stack)) {
				if (!simulate) {
					addDynamicBigStack(stack, bigStackSlot);
				}
				return ItemStack.EMPTY;
			}
			return stack;
		}

		if (!isValidForBigStackSlot(stack, bigStackSlot))
			return stack;

		return filteredBigItemStacks.get(bigStackSlot).getStack().insertItem(stack, simulate, isInputSlot(slot));
	}

	@SuppressWarnings({"unused", "squid:S1172"})
	protected boolean isValidForDynamicStack(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		} else {
			validateSlotIndex(slot);

			if (slot >= getSlots() - 1) {
				return ItemStack.EMPTY;
			}

			int bigStackSlot = getBigStackSlot(slot);
			FilteredBigItemStack bigStack = filteredBigItemStacks.get(bigStackSlot).getStack();
			ItemStack currentStack = isInputSlot(slot) ? bigStack.getInputStack() : bigStack.getOutputStack();

			if (currentStack.isEmpty()) {
				return ItemStack.EMPTY;
			}

			int numberRemoved = Math.min(currentStack.getCount(), amount);

			ItemStack ret = ItemHandlerHelper.copyStackWithSize(currentStack, numberRemoved);
			if (!simulate) {
				ItemStack updatedStack = numberRemoved == currentStack.getCount() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(currentStack, currentStack.getCount() - numberRemoved);
				updateBigStack(slot, updatedStack, bigStackSlot);
			}
			return ret;
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		ListNBT nbtTagList = new ListNBT();
		for (RemovableStack removableStack : filteredBigItemStacks) {
			FilteredBigItemStack bigStack = removableStack.getStack();
			if (!bigStack.isEmpty()) {
				nbtTagList.add(bigStack.serializeNBT());
			}
		}

		CompoundNBT nbt = new CompoundNBT();
		nbt.put("Items", nbtTagList);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT itemTags = tagList.getCompound(i);

			if (i < filteredBigItemStacks.size()) {
				filteredBigItemStacks.get(i).getStack().deserializeNBT(itemTags);
			} else {
				FilteredBigItemStack bigStack = new FilteredBigItemStack(getDynamicStackLimit());
				bigStack.deserializeNBT(itemTags);
				filteredBigItemStacks.add(new RemovableStack(bigStack, true));
			}
		}
	}

	void setDynamicSlotNumber() {
		this.dynamicSlotNumber = true;
	}

	private void validateSlotIndex(int slot) {
		int slots = getSlots();
		if (slot < 0 || slot >= slots)
			throw new IllegalArgumentException(String.format(NOT_IN_RANGE_ERROR, "Slot", slot, filteredBigItemStacks.size() - 1));
	}

	public static class RemovableStack {
		private final FilteredBigItemStack stack;
		private final boolean canRemove;

		public RemovableStack(FilteredBigItemStack stack, boolean canRemove) {
			this.stack = stack;
			this.canRemove = canRemove;
		}

		public FilteredBigItemStack getStack() {
			return stack;
		}

		boolean canRemove() {
			return canRemove;
		}
	}

}
