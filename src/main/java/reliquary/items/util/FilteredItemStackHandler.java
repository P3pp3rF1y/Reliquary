package reliquary.items.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class FilteredItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {
	private boolean dynamicSlotNumber = false;
	private final List<FilteredItemStack> filteredItemStacks;
	private static final String NOT_IN_RANGE_ERROR = "%s %d not in valid range - (0, %d)";

	FilteredItemStackHandler(List<FilteredItemStack> filteredItemStacks) {
		this.filteredItemStacks = filteredItemStacks;
	}

	public void setTotalCount(int stackSlot, int count) {
		if (dynamicSlotNumber && count == 0 && filteredItemStacks.get(stackSlot).canRemove()) {
			filteredItemStacks.remove(stackSlot);
		} else {
			filteredItemStacks.get(stackSlot).setCount(count);
		}
	}

	public int getTotalAmount(int stackSlot) {
		validateStackSlot(stackSlot);
		return filteredItemStacks.get(stackSlot).getCount();
	}

	private void validateStackSlot(int stackSlot) {
		if (stackSlot < 0 || stackSlot >= filteredItemStacks.size()) {
			throw new IllegalArgumentException(String.format(NOT_IN_RANGE_ERROR, "Big Stack Slot", stackSlot, filteredItemStacks.size() - 1));
		}
	}

	public void markDirty() {
		filteredItemStacks.removeIf(stack -> stack.isEmpty() && stack.canRemove());
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if (filteredItemStacks.size() > slot) {
			if (!isValidForStackSlot(stack, slot)) {
				return;
			}
		} else if (alreadyExistsInAnotherSlot(stack, slot)) {
			return;
		}

		if (isValidForDynamicStack(stack)) {
			addDynamicStack(stack, slot);
		}

		updateStack(slot, stack);
	}

	protected boolean isValidForStackSlot(ItemStack stack, int stackSlot) {
		return ItemHandlerHelper.canItemStacksStack(filteredItemStacks.get(stackSlot).getFilterStack(), stack);
	}

	private void updateStack(int slot, ItemStack stack) {
		FilteredItemStack filteredStack = filteredItemStacks.get(slot);
		filteredStack.setCount(stack.getCount());

		if (filteredStack.isEmpty() && filteredStack.canRemove()) {
			filteredItemStacks.remove(slot);
		}
	}

	private void addDynamicStack(ItemStack stack, int stackSlot) {
		if (dynamicSlotNumber && !stack.isEmpty() && stackSlot == filteredItemStacks.size()) {
			ItemStack filter = stack.copy();
			filter.setCount(1);
			filteredItemStacks.add(new FilteredItemStack(filter, getDynamicStackLimit(), true));
		}
	}

	protected int getDynamicStackLimit() {
		return Integer.MAX_VALUE;
	}

	private boolean alreadyExistsInAnotherSlot(ItemStack stack, int stackSlot) {
		for (int i = 0; i < filteredItemStacks.size(); i++) {
			if (i != stackSlot && isValidForStackSlot(stack, i)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int getSlots() {
		return filteredItemStacks.size() + (dynamicSlotNumber ? 1 : 0);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	public int getStackSlots() {
		return filteredItemStacks.size();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		return isValidForStackSlot(stack, slot);
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return filteredItemStacks.get(slot).getStack();
	}

	void setFilteredStack(int stackSlot, FilteredItemStack filteredItemStack) {
		if (stackSlot < 0 || stackSlot > filteredItemStacks.size()) {
			throw new IllegalArgumentException(String.format(NOT_IN_RANGE_ERROR, "Filtered Stack Slot", stackSlot, filteredItemStacks.size()));
		} else if (stackSlot == filteredItemStacks.size()) {
			filteredItemStacks.add(filteredItemStack);
			return;
		}
		filteredItemStacks.set(stackSlot, filteredItemStack);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty() || stack.getCount() == 0) {
			return ItemStack.EMPTY;
		}

		validateSlotIndex(slot);

		if (slot == filteredItemStacks.size()) {
			if (!alreadyExistsInAnotherSlot(stack, slot) && isValidForDynamicStack(stack)) {
				if (!simulate) {
					addDynamicStack(stack, slot);
				} else {
					return ItemStack.EMPTY;
				}
			} else {
				return stack;
			}
		}

		if (!isValidForStackSlot(stack, slot)) {
			return stack;
		}

		return filteredItemStacks.get(slot).insertItem(stack, simulate);
	}

	@SuppressWarnings({"unused", "squid:S1172"})
	protected boolean isValidForDynamicStack(ItemStack stack) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		} else {
			validateSlotIndex(slot);

			if (slot >= getSlots()) {
				return ItemStack.EMPTY;
			}

			ItemStack currentStack = filteredItemStacks.get(slot).getStack();

			if (currentStack.isEmpty()) {
				return ItemStack.EMPTY;
			}

			int numberRemoved = Math.min(currentStack.getCount(), amount);

			ItemStack ret = ItemHandlerHelper.copyStackWithSize(currentStack, numberRemoved);
			if (!simulate) {
				ItemStack updatedStack = numberRemoved == currentStack.getCount() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(currentStack, currentStack.getCount() - numberRemoved);
				updateStack(slot, updatedStack);
			}
			return ret;
		}
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (FilteredItemStack filteredStack : filteredItemStacks) {
			nbtTagList.add(filteredStack.serializeNBT());
		}

		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);

			if (i < filteredItemStacks.size()) {
				filteredItemStacks.get(i).getStack().deserializeNBT(itemTags);
			} else {
				FilteredItemStack filteredStack = new FilteredItemStack(getDynamicStackLimit(), true);
				filteredStack.deserializeNBT(itemTags);
				filteredItemStacks.add(filteredStack);
			}
		}
	}

	void setDynamicSlotNumber() {
		dynamicSlotNumber = true;
	}

	private void validateSlotIndex(int slot) {
		int slots = getSlots();
		if (slot < 0 || slot >= slots) {
			throw new IllegalArgumentException(String.format(NOT_IN_RANGE_ERROR, "Slot", slot, filteredItemStacks.size() - 1));
		}
	}
}
