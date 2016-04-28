package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.hamcrest.core.IsNull;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

@PrepareForTest({ItemHandlerHelper.class, ItemStack.class})
@RunWith(PowerMockRunner.class)
public class FilteredItemStackHandlerTest extends PowerMockTestCase {

	@Mock
	private ItemStack itemStack;

	private FilteredItemStackHandler handler;

	@BeforeMethod
	public void setup() {
		PowerMockito.mockStatic(ItemStack.class);
		itemStack = mock(ItemStack.class);
		PowerMockito.when(itemStack.getMaxStackSize()).thenReturn(64);
		PowerMockito.when(itemStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});
	}

	@Test(dataProvider = "setTotalAmountUpdatesInputOutputStacks")
	public void setTotalAmountUpdatesInputOutputStacks(int limit, int totalAmount, int inputCount, int outputCount) {
		handler = new FilteredItemStackHandler(new int[] {limit}, new ItemStack[] {itemStack}, new int[] {1});

		handler.setTotalAmount(0, totalAmount);

		Assert.assertEquals(handler.getStackInSlot(0) == null ? null : handler.getStackInSlot(0).stackSize, inputCount == 0 ? null : inputCount);
		Assert.assertEquals(handler.getStackInSlot(1) == null ? null : handler.getStackInSlot(1).stackSize, outputCount == 0 ? null : outputCount);

	}

	@DataProvider(name = "setTotalAmountUpdatesInputOutputStacks")
	public Object[][] getDataForSetTotalAmountUpdatesInputOutputStacks() {
		return new Object[][] {{1000, 10, 0, 10}, {1000, 100, 0, 64}, {1000, 960, 24, 64}, {1000, 1000, 64, 64}};
	}

	@Test(dataProvider = "setStackInSlotUpdatesTotalAmountAndStacks")
	public void setStackInSlotUpdatesTotalAmountAndStacks(int limit, int initialTotal, int inputUpdate, int outputUpdate, int expectedTotal, int expectedInput, int expectedOutput) {
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		handler.setTotalAmount(0, initialTotal);
		if(handler.getStackInSlot(0) != null || inputUpdate > 0) {
			ItemStack inputStack = itemStack.copy();
			inputStack.stackSize = inputUpdate;
			handler.setStackInSlot(0, inputStack);
		}

		ItemStack outputStack = itemStack.copy();
		outputStack.stackSize = outputUpdate;
		handler.setStackInSlot(1, outputStack);

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0) == null ? 0 : handler.getStackInSlot(0).stackSize, expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1) == null ? 0 : handler.getStackInSlot(1).stackSize, expectedOutput);
	}

	@DataProvider(name = "setStackInSlotUpdatesTotalAmountAndStacks")
	public Object[][] getDataForSetStackInSlotUpdatesTotalAmountAndStacks() {
		return new Object[][] {{1000, 0, 0, 10, 10, 0, 10},
				{1000, 10, 0, 20, 20, 0, 20},
				{1000, 50, 0, 64, 64, 0, 64},
				{1000, 100, 0, 14, 50, 0, 50},
				{1000, 900, 10, 64, 910, 0, 64},
				{1000, 900, 50, 64, 950, 14, 64},
				{1000, 950, 64, 64, 1000, 64, 64}};
	}

	@Test(dataProvider = "insertItemUpdatesTotalAmountAndStacks")
	public void insertItemUpdatesTotalAmountAndStacks(int limit, int initialAmount, boolean inputSlot, int insertAmount, int expectedTotal, int expectedInput, int expectedOutput, int expectedReturn) {
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);
		PowerMockito.when(ItemStack.copyItemStack(any(ItemStack.class))).thenReturn(mock(ItemStack.class));

		handler.setTotalAmount(0, initialAmount);

		ItemStack inputStack = itemStack.copy();
		inputStack.stackSize = insertAmount;
		ItemStack returned = handler.insertItem(inputSlot ? 0 : 1, inputStack, false);

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0) == null ? 0 : handler.getStackInSlot(0).stackSize, expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1) == null ? 0 : handler.getStackInSlot(1).stackSize, expectedOutput);
		Assert.assertEquals(returned == null ? 0 : returned.stackSize, expectedReturn);
	}

	@DataProvider(name = "insertItemUpdatesTotalAmountAndStacks")
	public Object[][] getDataForInsertItemUpdatesTotalAmountAndStacks() {
		return new Object[][] {{1000, 0, true, 10, 10, 0, 10, 0},
				{1000, 10, true, 10, 20, 0, 20, 0},
				{1000, 50, true, 20, 70, 0, 64, 0},
				{1000, 50, false, 20, 64, 0, 64, 6},
				{1000, 900, true, 50, 950, 14, 64, 0},
				{1000, 950, true, 64, 1000, 64, 64, 14}};
	}

	@Test
	public void insertItemAllowsValidItemsToBeInserted() {
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		ItemStack insertedStack = itemStack.copy();
		insertedStack.stackSize = 64;

		ItemStack returnedStack = handler.insertItem(0, insertedStack, false);

		Assert.assertNull(returnedStack);
	}

	@Test
	public void insertItemDoesntAllowInvalidItemsToBeInserted() {
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(false);

		ItemStack insertedStack = itemStack.copy();
		insertedStack.stackSize = 64;

		ItemStack returnedStack = handler.insertItem(0, insertedStack, false);

		Assert.assertEquals(returnedStack.stackSize, insertedStack.stackSize);
	}

	@Test(dataProvider = "extractItemUpdatesTotalAmountAndStacks")
	public void extractItemUpdatesTotalAmountAndStacks(int limit, int initialAmount, boolean inputSlot, int extractAmount, int expectedTotal, int expectedInput, int expectedOutput, int expectedReturn) {
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);
		PowerMockito.when(ItemStack.copyItemStack(any(ItemStack.class))).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		handler.setTotalAmount(0, initialAmount);
		ItemStack returned = handler.extractItem(inputSlot ? 0 : 1, extractAmount, false);

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0) == null ? 0 : handler.getStackInSlot(0).stackSize, expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1) == null ? 0 : handler.getStackInSlot(1).stackSize, expectedOutput);
		Assert.assertEquals(returned == null ? 0 : returned.stackSize, expectedReturn);
	}

	@DataProvider(name = "extractItemUpdatesTotalAmountAndStacks")
	public Object[][] getDataForExtractItemUpdatesTotalAmountAndStacks() {
		return new Object[][] {{1000, 64, true, 30, 64, 0, 64, 0},
				{1000, 64, false, 30, 34, 0, 34, 30},
				{1000, 34, false, 64, 0, 0, 0, 34},
				{1000, 950, true, 10, 940, 4, 64, 10},
				{1000, 950, true, 20, 936, 0, 64, 14},
				{1000, 950, false, 20, 930, 0, 64, 20},
				{1000, 950, false, 10, 940, 4, 64, 10},
				{1000, 1000, true, 64, 936, 0, 64, 64},
				{1000, 1000, false, 64, 936, 0, 64, 64},
				{1000, 0, false, 64, 0, 0, 0, 0},
				{1000, 0, true, 64, 0, 0, 0, 0}};
	}

	@Test(dataProvider = "markDirtyUpdatesTotalAmountAndStacks")
	public void markDirtyUpdatesTotalAmountAndStacks(int limit, int initialTotal, boolean inputSlot, int amount, int expectedTotal, int expectedInput, int expectedOutput) {
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		handler.setTotalAmount(0, initialTotal);

		int slot = (inputSlot ? 0 : 1);
		ItemStack stack = handler.getStackInSlot(slot);
		if(stack == null) {
			stack = itemStack.copy();
			stack.stackSize = amount;
			handler.setStackInSlot(0, stack);
		} else {
			stack.stackSize = amount;
		}

		handler.markDirty();

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0) == null ? 0 : handler.getStackInSlot(0).stackSize, expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1) == null ? 0 : handler.getStackInSlot(1).stackSize, expectedOutput);
	}

	@DataProvider(name = "markDirtyUpdatesTotalAmountAndStacks")
	public Object[][] getDataForMarkDirtyUpdatesTotalAmountAndStacks() {
		return new Object[][] {{1000, 0, false, 10, 10, 0, 10},
				{1000, 0, true, 10, 10, 0, 10},
				{1000, 10, false, 20, 20, 0, 20},
				{1000, 10, true, 64, 74, 0, 64},
				{1000, 50, true, 20, 70, 0, 64},
				{1000, 900, true, 30, 930, 0, 64},
				{1000, 930, true, 20, 950, 14, 64},
				{1000, 950, true, 64, 1000, 64, 64},
				{1000, 1000, true, 0, 936, 0, 64},
				{1000, 1000, false, 0, 936, 0, 64},
				{1000, 950, true, 0, 936, 0, 64},
				{1000, 128, false, 0, 64, 0, 64},
				{1000, 80, false, 0, 16, 0, 16},
				{1000, 64, false, 0, 0, 0, 0}

		};
	}

	//dynamic size

	@Test
	public void setTotalAmountToZeroRemovesDynamicStack() throws RuntimeException {
		ItemStack dynamicItemStack = mock(ItemStack.class);
		PowerMockito.when(dynamicItemStack.getMaxStackSize()).thenReturn(64);
		PowerMockito.when(dynamicItemStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		handler = spy(new FilteredItemStackHandler(new int[] {1000, 1000}, new ItemStack[] {itemStack, dynamicItemStack}, new int[] {1, 1}));
		handler.setDynamicSize(true);

		when(handler.getParentSlotRemovable(0)).thenReturn(false);
		when(handler.getParentSlotRemovable(1)).thenReturn(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 10);

		handler.setTotalAmount(1, 0);

		Assert.assertEquals(handler.getStackInSlot(1).stackSize, 55);
		Assert.assertEquals(handler.getSlots(), 4);
	}

	//TODO: add checks for invalid item stacks being added/set (probably just different data set with false)

	@Test
	public void setStackInSlotAddsDynamicStack() throws RuntimeException {
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, new ItemStack[] {itemStack}, new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);
		when(handler.getParentSlotUnitWorth(anyInt())).thenReturn(1);
		when(handler.getParentSlotLimit(anyInt())).thenReturn(1000);
		handler.setDynamicSize(true);

		handler.setTotalAmount(0, 55);

		ItemStack newStack = mock(ItemStack.class);
		PowerMockito.when(newStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		newStack.stackSize = 10;

		handler.setStackInSlot(2, newStack);

		Assert.assertEquals(handler.getStackInSlot(1).stackSize, 55);
		Assert.assertEquals(handler.getTotalAmount(1), 10);
		Assert.assertEquals(handler.getSlots(), 6);
	}

	@Test
	public void setStackInSlotDoesntAddDuplicateDynamicStack() {
		ItemStack dynamicStack = mock(ItemStack.class);
		PowerMockito.when(dynamicStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		handler = spy(new FilteredItemStackHandler(new int[] {1000, 1000}, new ItemStack[] {itemStack, dynamicStack}, new int[] {1, 1}));
		doReturn(true).when(handler).isItemStackValidForParentSlot(any(ItemStack.class), anyInt());
		when(handler.getParentSlotUnitWorth(anyInt())).thenReturn(1);
		when(handler.getParentSlotLimit(anyInt())).thenReturn(1000);

		PowerMockito.mockStatic(ItemHandlerHelper.class);
		PowerMockito.when(ItemHandlerHelper.canItemStacksStack(any(ItemStack.class), any(ItemStack.class))).thenReturn(true);
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 1000);

		ItemStack newStack = mock(ItemStack.class);
		PowerMockito.when(newStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		newStack.stackSize = 10;

		handler.setStackInSlot(4, newStack);

		Assert.assertEquals(handler.getStackInSlot(1).stackSize, 55);
		Assert.assertEquals(handler.getTotalAmount(1), 1000);
		Assert.assertEquals(handler.getSlots(), 6);
	}

	@Test
	public void setLastStackInSlotToNullRemovesDynamicStack() {
		ItemStack dynamicStack = mock(ItemStack.class);
		PowerMockito.when(dynamicStack.getMaxStackSize()).thenReturn(64);
		PowerMockito.when(dynamicStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		handler = new FilteredItemStackHandler(new int[] {1000, 1000}, new ItemStack[] {itemStack, dynamicStack}, new int[] {1, 1});
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 64);

		handler.setStackInSlot(3, null);

		handler.markDirty();

		Assert.assertEquals(handler.getSlots(), 4);
		Assert.assertEquals(handler.getTotalAmount(0), 55);
	}

	@Test
	public void insertItemAddsDynamicStack() {
		handler = new FilteredItemStackHandler(new int[] {1000}, new ItemStack[] {itemStack}, new int[] {1});
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);

	}

	//insertItemAddsDynamicStack

	//insertItemDoesntAddDuplicateDynamicStack

	//extractItemRemovesDynamicStackOnRemainderUnitsRemoval

	//markDirtyAddsDynamicStack

	//markDirtyRemovesDynamicStack

	//TODO add unit worth

}
