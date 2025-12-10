package reliquary.item.component;

import com.google.common.base.Preconditions;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class OversizedComponentItemHandler implements ResourceHandler<ItemResource> {
	protected final ItemStack parent;
	protected final DataComponentType<OversizedItemContainerContents> component;
	protected final int size;
	private final BiFunction<ItemStack, Integer, Integer> getSlotLimit;
	private final BiPredicate<Integer, ItemStack> isItemValid;
	private final ContentsJournal snapshotJournal = new ContentsJournal();

	public OversizedComponentItemHandler(ItemStack parent, DataComponentType<OversizedItemContainerContents> component, int size, BiFunction<ItemStack, Integer, Integer> getSlotLimit, BiPredicate<Integer, ItemStack> isItemValid) {
		this.parent = parent;
		this.component = component;
		this.size = size;
		this.getSlotLimit = getSlotLimit;
		this.isItemValid = isItemValid;
		Preconditions.checkArgument(size <= OversizedItemContainerContents.MAX_SIZE, "The max size of OversizedItemContainerContents is " + OversizedItemContainerContents.MAX_SIZE + " slots.");
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public ItemResource getResource(int i) {
		return ItemResource.of(getStackFromContents(getContents(), i));
	}

	@Override
	public long getAmountAsLong(int i) {
		return getStackFromContents(getContents(), i).getCount();
	}

	public ItemStack getStackInSlot(int slot) {
		return getStackFromContents(getContents(), slot);
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		validateSlotIndex(slot);
		if (!isItemValid.test(slot, stack)) {
			throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
		} else {
			OversizedItemContainerContents contents = getContents();
			ItemStack existing = getStackFromContents(contents, slot);
			if (!ItemStack.matches(stack, existing)) {
				updateContents(contents, stack, slot);
			}
		}
	}

	public ItemStack insertItemOrAddIntoNewSlotIfNoStackMatches(ItemStack stack) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		for (int slot = 0; slot < size(); slot++) {
			if (ItemStack.isSameItemSameComponents(stack, getStackInSlot(slot)) || (isItemValid.test(slot, stack) && getStackInSlot(slot).isEmpty())) {
				try (Transaction tx = Transaction.openRoot()) {
					ItemStack remaining = insertItem(slot, stack, tx);
					if (remaining.getCount() != stack.getCount()) {
						tx.commit();
					}
					return remaining;
				}
			}
		}

		OversizedItemContainerContents contents = this.getContents();
		int newSlot = this.size();
		NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), this.size() + 1), ItemStack.EMPTY);
		if (!isItemValid.test(newSlot, stack)) {
			return stack;
		}
		contents.copyInto(list);
		int countToAdd = Math.min(stack.getCount(), getCapacityAsInt(list.size(), ItemResource.EMPTY));
		ItemStack stackToAdd = stack.copyWithCount(countToAdd);
		list.set(newSlot, stackToAdd);
		this.parent.set(this.component, OversizedItemContainerContents.fromItems(list));
		return countToAdd == stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - countToAdd);
	}

	public ItemStack extractItemAndRemoveSlotIfEmpty(int slot, int amount, boolean simulate) {
		try (Transaction tx = Transaction.openRoot()) {
			ItemStack extracted = extractItemAndRemoveSlotIfEmpty(slot, amount, tx);
			if (!simulate) {
				tx.commit();
			}
			return extracted;
		}
	}

	public ItemStack extractItemAndRemoveSlotIfEmpty(int slot, int amount, TransactionContext tx) {
		ItemStack stack = this.extractItem(slot, amount, tx);
		if (getStackInSlot(slot).isEmpty()) {
			OversizedItemContainerContents contents = this.getContents();
			NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), this.size()), ItemStack.EMPTY);
			for (int i = 0; i < slot; i++) {
				list.set(i, contents.getSlots() > i ? contents.getStackInSlot(i) : ItemStack.EMPTY);
			}
			for (int i = slot; i < list.size(); i++) {
				list.set(i, contents.getSlots() > i + 1 ? contents.getStackInSlot(i + 1) : ItemStack.EMPTY);
			}
			snapshotJournal.updateSnapshots(tx);
			this.parent.set(this.component, OversizedItemContainerContents.fromItems(list));
		}
		return stack;

	}

	public void removeSlot(int slot) {
		OversizedItemContainerContents contents = this.getContents();
		List<ItemStack> list = new ArrayList<>(NonNullList.withSize(Math.max(contents.getSlots(), this.size()), ItemStack.EMPTY));
		contents.copyInto(list);
		list.remove(slot);
		parent.set(this.component, OversizedItemContainerContents.fromItems(list));
	}

	@Override
	public int insert(int i, ItemResource resource, int amount, TransactionContext tx) {
		ItemStack remainder = insertItem(i, resource.toStack(amount), tx);
		return amount - remainder.getCount();
	}

	private ItemStack insertItem(int slot, ItemStack toInsert, TransactionContext tx) {
		this.validateSlotIndex(slot);
		if (toInsert.isEmpty()) {
			return ItemStack.EMPTY;
		} else if (!isItemValid.test(slot, toInsert)) {
			return toInsert;
		} else {
			OversizedItemContainerContents contents = this.getContents();
			ItemStack existing = this.getStackFromContents(contents, slot);
			int insertLimit = getCapacityAsInt(slot, ItemResource.EMPTY);
			if (!existing.isEmpty()) {
				if (!ItemStack.isSameItemSameComponents(toInsert, existing)) {
					return toInsert;
				}

				insertLimit -= existing.getCount();
			}

			if (insertLimit <= 0) {
				return toInsert;
			} else {
				int inserted = Math.min(insertLimit, toInsert.getCount());
				snapshotJournal.updateSnapshots(tx);
				this.updateContents(contents, toInsert.copyWithCount(existing.getCount() + inserted), slot);

				return toInsert.copyWithCount(toInsert.getCount() - inserted);
			}
		}
	}

	@Override
	public int extract(int i, ItemResource resource, int i1, TransactionContext tx) {
		return extractItem(i, i1, tx).getCount();
	}

	public ItemStack extractItem(int slot, int amount) {
		try (Transaction tx = Transaction.openRoot()) {
			ItemStack extracted = extractItem(slot, amount, tx);
			if (extracted.getCount() > 0) {
				tx.commit();
			}
			return extracted;
		}
	}

	public ItemStack extractItem(int slot, int amount, TransactionContext tx) {
		this.validateSlotIndex(slot);
		if (amount == 0) {
			return ItemStack.EMPTY;
		} else {
			OversizedItemContainerContents contents = this.getContents();
			ItemStack existing = this.getStackFromContents(contents, slot);
			if (existing.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				int toExtract = Math.min(Math.min(amount, existing.getMaxStackSize()), existing.getCount());
				snapshotJournal.updateSnapshots(tx);
				this.updateContents(contents, existing.copyWithCount(existing.getCount() - toExtract), slot);

				return existing.copyWithCount(toExtract);
			}
		}
	}

	@Override
	public long getCapacityAsLong(int i, ItemResource resource) {
		return getSlotLimit.apply(parent, i);
	}

	@Override
	public boolean isValid(int i, ItemResource resource) {
		return resource.isEmpty() || isItemValid.test(i, resource.toStack()) && resource.getItem().canFitInsideContainerItems();
	}

	protected OversizedItemContainerContents getContents() {
		if (this.parent.has(this.component)) {
			return this.parent.get(this.component);
		}

		return OversizedItemContainerContents.fromSize(size);
	}

	protected ItemStack getStackFromContents(OversizedItemContainerContents contents, int slot) {
		this.validateSlotIndex(slot);
		return contents.getSlots() <= slot ? ItemStack.EMPTY : contents.getStackInSlot(slot);
	}

	protected void updateContents(OversizedItemContainerContents contents, ItemStack stack, int slot) {
		this.validateSlotIndex(slot);
		NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), this.size()), ItemStack.EMPTY);
		contents.copyInto(list);
		ItemStack oldStack = list.get(slot);
		list.set(slot, stack);
		this.parent.set(this.component, OversizedItemContainerContents.fromItems(list));
	}

	protected final void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= this.size()) {
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.size() + ")");
		}
	}

	//TODO  need to call  ((StackJournal)this.snapshotJournals.get(index)).updateSnapshots(transaction);

	private class ContentsJournal extends SnapshotJournal<OversizedItemContainerContents> {
		protected OversizedItemContainerContents createSnapshot() {
			return getContents().copy();
		}

		protected void revertToSnapshot(OversizedItemContainerContents snapshot) {
			parent.set(component, snapshot);
		}
	}
}
