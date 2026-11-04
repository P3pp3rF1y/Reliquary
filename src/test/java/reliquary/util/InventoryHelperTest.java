package reliquary.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InventoryHelperTest {
	@Test
	void tryToRemoveFromInventoryStopsAfterSlotDrains() {
		ItemStack contents = stack(Items.FEATHER);
		SingleSlotItemHandler handler = new SingleSlotItemHandler(contents, 2, false);

		int removed = InventoryHelper.tryToRemoveFromInventory(contents, handler, 4);

		assertEquals(2, removed);
		assertEquals(1, handler.extractCalls);
		assertFalse(handler.emptyResourceExtracted);
	}

	@Test
	void tryToRemoveFromInventoryContinuesExtractingFromRefillingSlot() {
		ItemStack contents = stack(Items.FEATHER);
		SingleSlotItemHandler handler = new SingleSlotItemHandler(contents, 2, true);

		int removed = InventoryHelper.tryToRemoveFromInventory(contents, handler, 5);

		assertEquals(5, removed);
		assertEquals(3, handler.extractCalls);
		assertFalse(handler.emptyResourceExtracted);
	}

	private ItemStack stack(Item item) {
		return new ItemStack(Holder.direct(item, DataComponentMap.EMPTY), 1, DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 64).build());
	}

	private static class SingleSlotItemHandler implements ResourceHandler<ItemResource> {
		private final ItemResource resource;
		private final int refillAmount;
		private final boolean refills;
		private int amount;
		private int extractCalls;
		private boolean emptyResourceExtracted;

		private SingleSlotItemHandler(ItemStack contents, int amount, boolean refills) {
			resource = ItemResource.of(contents.typeHolder(), DataComponentPatch.builder().set(DataComponents.MAX_STACK_SIZE, 64).build());
			refillAmount = amount;
			this.amount = amount;
			this.refills = refills;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public ItemResource getResource(int index) {
			return amount == 0 ? ItemResource.EMPTY : resource;
		}

		@Override
		public long getAmountAsLong(int index) {
			return amount;
		}

		@Override
		public long getCapacityAsLong(int index, ItemResource resource) {
			return refillAmount;
		}

		@Override
		public boolean isValid(int index, ItemResource resource) {
			return this.resource.equals(resource);
		}

		@Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if (resource.isEmpty()) {
				emptyResourceExtracted = true;
				return 0;
			}
			if (!this.resource.equals(resource)) {
				return 0;
			}

			extractCalls++;
			int extracted = Math.min(this.amount, amount);
			this.amount = refills ? refillAmount : this.amount - extracted;
			return extracted;
		}
	}
}
