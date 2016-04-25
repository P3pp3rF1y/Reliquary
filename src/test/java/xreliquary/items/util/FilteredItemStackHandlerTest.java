package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

public class FilteredItemStackHandlerTest {

	@Mock
	private ItemStack itemStack;

	private FilteredItemStackHandler handler;

	@BeforeMethod
	public void setup() {
	}

	@Test (dataProvider = "setTotalAmountUpdatesInputOutputStacks")
	public void setTotalAmountUpdatesInputOutputStacks(int limit, int totalAmount, int inputCount, int outputCount) {
		when(itemStack.getMaxStackSize()).thenReturn(64);
		handler = new FilteredItemStackHandler(new int[]{limit}, new ItemStack[] {itemStack}, new int[] {1});

		handler.setTotalAmount(0, totalAmount);

		Assert.assertEquals(inputCount == 0 ? null : inputCount, handler.getStackInSlot(0) == null ? null : handler.getStackInSlot(0));
		Assert.assertEquals(outputCount == 0 ? null : outputCount, handler.getStackInSlot(1) == null ? null : handler.getStackInSlot(1));

	}

	@DataProvider(name="setTotalAmountUpdatesInputOutputStacks")
	public Object[][] getDataForSetTotalAmountUpdatesInputOutputStacks() {
		return new Object[][] {
				{1000, 10, 0, 10},
				{1000, 100, 0, 64},
				{1000, 960, 24, 64},
				{1000, 1000, 64, 64}
		};
	}

	//setStackInSlotUpdatesTotalAmountAndStacks

	//insertItemUpdatesTotalAmountAndStacks

	//insertItemOnlyAllowsValidItemsToBeInserted

	//extractItemUpdatesTotalAmountAndStacks

	//dynamic size

	//setTotalAmountToZeroRemovesDynamicStack

	//setStackInSlotAddsDynamicStack

	//setStackInSlotDoesntAddDuplicateDynamicStack

	//setStackInSlotToNullRemovesDynamicStack

	//insertItemAddsDynamicStack

	//insertItemDoesntAddDuplicateDynamicStack

	//extractItemRemovesDynamicStackOnRemainderUnitsRemoval

	//TODO add unit worth

}
