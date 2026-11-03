package reliquary.item.component;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OversizedComponentItemHandlerTest {
	@Test
	void setStackInSlotClearsPopulatedSlotWhenGivenEmptyStack() {
		OversizedComponentItemHandler handler = createHandler();
		handler.setStackInSlot(0, stack(Items.FEATHER));

		assertTrue(handler.getStackInSlot(0).is(Items.FEATHER));

		assertDoesNotThrow(() -> handler.setStackInSlot(0, ItemStack.EMPTY));

		assertTrue(handler.getStackInSlot(0).isEmpty());
	}

	@Test
	void setStackInSlotRejectsInvalidNonEmptyStack() {
		OversizedComponentItemHandler handler = createHandler();

		assertThrows(RuntimeException.class, () -> handler.setStackInSlot(0, stack(Items.STICK)));
	}

	private OversizedComponentItemHandler createHandler() {
		DataComponentType<OversizedItemContainerContents> component = DataComponentType.<OversizedItemContainerContents>builder()
				.persistent(OversizedItemContainerContents.CODEC).build();
		return new OversizedComponentItemHandler(stack(Items.CHEST), component, 1, (parent, slot) -> 64, (slot, stack) -> stack.is(Items.FEATHER));
	}

	private ItemStack stack(Item item) {
		return new ItemStack(Holder.direct(item, DataComponentMap.builder().set(DataComponents.MAX_STACK_SIZE, 64).build()));
	}
}
