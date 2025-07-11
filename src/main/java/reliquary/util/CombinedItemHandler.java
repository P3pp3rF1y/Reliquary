package reliquary.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

// combines multiple IItemHandler into one interface
public class CombinedItemHandler implements IItemHandler {

	protected final IItemHandler[] itemHandler; // the handlers
	protected final int[] baseIndex; // index-offsets of the different handlers
	protected final int slotCount; // number of total slots

	public CombinedItemHandler(IItemHandler... itemHandler) {
		this.itemHandler = itemHandler;
		baseIndex = new int[itemHandler.length];
		int index = 0;
		for (int i = 0; i < itemHandler.length; i++) {
			index += itemHandler[i].getSlots();
			baseIndex[i] = index;
		}
		slotCount = index;
	}

	// returns the handler index for the slot
	protected int getIndexForSlot(int slot) {
		if (slot < 0) {
			return -1;
		}

		for (int i = 0; i < baseIndex.length; i++) {
			if (slot - baseIndex[i] < 0) {
				return i;
			}
		}
		return -1;
	}

	protected IItemHandler getHandlerFromIndex(int index) {
		if (index < 0 || index >= itemHandler.length) {
			return EmptyItemHandler.INSTANCE;
		}
		return itemHandler[index];
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length) {
			return slot;
		}
		return slot - baseIndex[index - 1];
	}

	@Override
	public int getSlots() {
		return slotCount;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		int index = getIndexForSlot(slot);
		IItemHandler handler = getHandlerFromIndex(index);
		slot = getSlotFromIndex(slot, index);
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		int index = getIndexForSlot(slot);
		IItemHandler handler = getHandlerFromIndex(index);
		slot = getSlotFromIndex(slot, index);
		return handler.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		int index = getIndexForSlot(slot);
		IItemHandler handler = getHandlerFromIndex(index);
		slot = getSlotFromIndex(slot, index);
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		int index = getIndexForSlot(slot);
		IItemHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(slot, index);
		return handler.getSlotLimit(localSlot);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		int index = getIndexForSlot(slot);
		IItemHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(slot, index);
		return handler.isItemValid(localSlot, stack);
	}
}
