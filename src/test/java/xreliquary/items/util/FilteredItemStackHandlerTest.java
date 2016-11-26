/* TODO rewrite tests
package xreliquary.items.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

*/
/*@PowerMockIgnore({"javax.management.*"})
@RunWith(PowerMockRunner.class)*//*

@PrepareForTest({ItemHandlerHelper.class})
public class FilteredItemStackHandlerTest extends PowerMockTestCase {

	private Item itemA;
	private ItemStack itemStack;

	private FilteredItemStackHandler handler;

	@BeforeMethod
	public void setup() {
		itemA = mock(Item.class);
		itemStack = new ItemStack(itemA);
*/
/*		PowerMockito.mockStatic(ItemStack.class);
		itemStack = mock(ItemStack.class);
		PowerMockito.when(itemStack.getMaxStackSize()).thenReturn(64);
		PowerMockito.when(itemStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});*//*

	}

	@Test(dataProvider = "setTotalAmountUpdatesInputOutputStacks")
	public void setTotalAmountUpdatesInputOutputStacks(int limit, int totalAmount, int inputCount, int outputCount) {
		handler = new FilteredItemStackHandler(new int[] {limit}, NonNullList.withSize(1, itemStack), new int[] {1});

		handler.setTotalAmount(0, totalAmount);

		Assert.assertEquals(handler.getStackInSlot(0).isEmpty() ? null : handler.getStackInSlot(0).getCount(), inputCount == 0 ? null : inputCount);
		Assert.assertEquals(handler.getStackInSlot(1).isEmpty() ? null : handler.getStackInSlot(1).getCount(), outputCount == 0 ? null : outputCount);

	}

	@DataProvider(name = "setTotalAmountUpdatesInputOutputStacks")
	public Object[][] getDataForSetTotalAmountUpdatesInputOutputStacks() {
		return new Object[][] {{1000, 10, 0, 10}, {1000, 100, 0, 64}, {1000, 960, 24, 64}, {1000, 1000, 64, 64}};
	}

	@Test(dataProvider = "setStackInSlotUpdatesTotalAmountAndStacks")
	public void setStackInSlotUpdatesTotalAmountAndStacks(int limit, int initialTotal, int inputUpdate, int outputUpdate, int expectedTotal, int expectedInput, int expectedOutput) {
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, NonNullList.withSize(1, itemStack), new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		handler.setTotalAmount(0, initialTotal);
		if(!handler.getStackInSlot(0).isEmpty() || inputUpdate > 0) {
			ItemStack inputStack = itemStack.copy();
			inputStack.setCount(inputUpdate);
			handler.setStackInSlot(0, inputStack);
		}

		ItemStack outputStack = itemStack.copy();
		outputStack.setCount(outputUpdate);
		handler.setStackInSlot(1, outputStack);

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0).isEmpty() ? 0 : handler.getStackInSlot(0).getCount(), expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1).isEmpty() ? 0 : handler.getStackInSlot(1).getCount(), expectedOutput);
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
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, NonNullList.withSize(1, itemStack), new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		handler.setTotalAmount(0, initialAmount);

		ItemStack inputStack = itemStack.copy();
		inputStack.setCount(insertAmount);
		ItemStack returned = handler.insertItem(inputSlot ? 0 : 1, inputStack, false);

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0).isEmpty() ? 0 : handler.getStackInSlot(0).getCount(), expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1).isEmpty() ? 0 : handler.getStackInSlot(1).getCount(), expectedOutput);
		Assert.assertEquals(returned.isEmpty() ? 0 : returned.getCount(), expectedReturn);
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
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, NonNullList.withSize(1, itemStack), new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		ItemStack insertedStack = itemStack.copy();
		insertedStack.setCount(64);

		ItemStack returnedStack = handler.insertItem(0, insertedStack, false);

		Assert.assertNull(returnedStack);
	}

	@Test
	public void insertItemDoesntAllowInvalidItemsToBeInserted() {
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, NonNullList.withSize(1, itemStack), new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(false);

		ItemStack insertedStack = itemStack.copy();
		insertedStack.setCount(64);

		ItemStack returnedStack = handler.insertItem(0, insertedStack, false);

		Assert.assertEquals(returnedStack.getCount(), insertedStack.getCount());
	}

	@Test(dataProvider = "extractItemUpdatesTotalAmountAndStacks")
	public void extractItemUpdatesTotalAmountAndStacks(int limit, int initialAmount, boolean inputSlot, int extractAmount, int expectedTotal, int expectedInput, int expectedOutput, int expectedReturn) {
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, NonNullList.withSize(1, itemStack), new int[] {1}));
		doReturn(true).when(handler).isItemStackValidForParentSlot(isNotNull(ItemStack.class), anyInt());

		handler.setTotalAmount(0, initialAmount);
		ItemStack returned = handler.extractItem(inputSlot ? 0 : 1, extractAmount, false);

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0).isEmpty() ? 0 : handler.getStackInSlot(0).getCount(), expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1).isEmpty() ? 0 : handler.getStackInSlot(1).getCount(), expectedOutput);
		Assert.assertEquals(returned.isEmpty() ? 0 : returned.getCount(), expectedReturn);
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
		handler = spy(new FilteredItemStackHandler(new int[] {limit}, NonNullList.withSize(1, itemStack), new int[] {1}));
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);

		handler.setTotalAmount(0, initialTotal);

		int slot = (inputSlot ? 0 : 1);
		ItemStack stack = handler.getStackInSlot(slot);
		if(stack.isEmpty()) {
			stack = itemStack.copy();
			stack.setCount(amount);
			handler.setStackInSlot(0, stack);
		} else {
			stack.setCount(amount);
		}

		handler.markDirty();

		Assert.assertEquals(handler.getTotalAmount(0), expectedTotal);
		Assert.assertEquals(handler.getStackInSlot(0).isEmpty() ? 0 : handler.getStackInSlot(0).getCount(), expectedInput);
		Assert.assertEquals(handler.getStackInSlot(1).isEmpty() ? 0 : handler.getStackInSlot(1).getCount(), expectedOutput);
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

		NonNullList<ItemStack> filterStacks = NonNullList.create();
		filterStacks.add(itemStack);
		filterStacks.add(dynamicItemStack);
		handler = spy(new FilteredItemStackHandler(new int[] {1000, 1000}, filterStacks, new int[] {1, 1}));
		handler.setDynamicSize(true);

		when(handler.getParentSlotRemovable(0)).thenReturn(false);
		when(handler.getParentSlotRemovable(1)).thenReturn(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 10);

		handler.setTotalAmount(1, 0);

		Assert.assertEquals(handler.getStackInSlot(1).getCount(), 55);
		Assert.assertEquals(handler.getSlots(), 4);
	}

	//TODO: add checks for invalid item stacks being added/set (probably just different data set with false)
	//TODO add data for dynamic stack addition in case of both input and output slots

	@Test
	public void setStackInSlotAddsDynamicStack() throws RuntimeException {
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, NonNullList.withSize(1, itemStack), new int[] {1}));
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

		newStack.setCount(10);

		handler.setStackInSlot(2, newStack);

		Assert.assertEquals(handler.getStackInSlot(1).getCount(), 55);
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

		NonNullList<ItemStack> filterStacks = NonNullList.create();
		filterStacks.add(itemStack);
		filterStacks.add(dynamicStack);
		handler = spy(new FilteredItemStackHandler(new int[] {1000, 1000}, filterStacks, new int[] {1, 1}));
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

		newStack.setCount(10);

		handler.setStackInSlot(4, newStack);

		Assert.assertEquals(handler.getStackInSlot(1).getCount(), 55);
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

		NonNullList<ItemStack> filterStacks = NonNullList.create();
		filterStacks.add(itemStack);
		filterStacks.add(dynamicStack);
		handler = new FilteredItemStackHandler(new int[] {1000, 1000}, filterStacks, new int[] {1, 1});
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 64);

		handler.setStackInSlot(3, ItemStack.EMPTY);

		handler.markDirty();

		Assert.assertEquals(handler.getSlots(), 4);
		Assert.assertEquals(handler.getTotalAmount(0), 55);
	}

	@Test
	public void insertItemAddsDynamicStack() {
		handler = spy(new FilteredItemStackHandler(new int[] {1000}, NonNullList.withSize(1, itemStack), new int[] {1}));
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);
		when(handler.isItemStackValidForParentSlot(any(ItemStack.class), anyInt())).thenReturn(true);
		when(handler.getParentSlotLimit(anyInt())).thenReturn(1000);
		when(handler.getParentSlotUnitWorth(anyInt())).thenReturn(1);

		ItemStack newStack = mock(ItemStack.class);
		PowerMockito.when(newStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		when(newStack.getMaxStackSize()).thenReturn(64);

		newStack.setCount(10);

		handler.insertItem(3, newStack, false);

		Assert.assertEquals(handler.getSlots(), 6);
		Assert.assertEquals(handler.getTotalAmount(0), 55);
		Assert.assertEquals(handler.getTotalAmount(1), 10);
	}

	@Test
	public void insertItemDoesntAddDuplicateDynamicStack() {
		ItemStack dynamicStack = mock(ItemStack.class);
		PowerMockito.when(dynamicStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		NonNullList<ItemStack> filterStacks = NonNullList.create();
		filterStacks.add(itemStack);
		filterStacks.add(dynamicStack);
		handler = spy(new FilteredItemStackHandler(new int[] {1000, 1000}, filterStacks, new int[] {1, 1}));
		doReturn(true).when(handler).isItemStackValidForParentSlot(any(ItemStack.class), anyInt());

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

		newStack.setCount(10);

		handler.insertItem(4, newStack, false);

		Assert.assertEquals(handler.getStackInSlot(1).getCount(), 55);
		Assert.assertEquals(handler.getTotalAmount(1), 1000);
		Assert.assertEquals(handler.getSlots(), 6);
	}

	@Test
	public void extractItemRemovesDynamicStackOnRemainderUnitsRemoval() {
		ItemStack dynamicStack = new ItemStack(Items.WHEAT_SEEDS);

		NonNullList<ItemStack> filterStacks = NonNullList.create();
		filterStacks.add(itemStack);
		filterStacks.add(dynamicStack);
		handler = new FilteredItemStackHandler(new int[] {1000, 1000}, filterStacks, new int[] {1, 1});
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 64);

		ItemStack returned = handler.extractItem(3, 64, false);

		Assert.assertEquals(returned.getCount(), 64);
		Assert.assertEquals(handler.getSlots(), 4);
		Assert.assertEquals(handler.getTotalAmount(0), 55);
	}

	@Test
	public void markDirtyRemovesDynamicStack() {
		ItemStack dynamicStack = mock(ItemStack.class);
		PowerMockito.when(dynamicStack.getMaxStackSize()).thenReturn(64);
		PowerMockito.when(dynamicStack.copy()).thenAnswer(new Answer<ItemStack>() {
			@Override
			public ItemStack answer(InvocationOnMock invocation) throws Throwable {
				return mock(ItemStack.class);
			}
		});

		NonNullList<ItemStack> filterStacks = NonNullList.create();
		filterStacks.add(itemStack);
		filterStacks.add(dynamicStack);
		handler = spy(new FilteredItemStackHandler(new int[] {1000, 1000}, filterStacks, new int[] {1, 1}));
		handler.setDynamicSize(true);
		handler.setTotalAmount(0, 55);
		handler.setTotalAmount(1, 64);
		when(handler.getParentSlotRemovable(1)).thenReturn(true);

		ItemStack stack = handler.getStackInSlot(3);
		stack.setCount(0);

		handler.markDirty();

		Assert.assertEquals(handler.getSlots(), 4);
		Assert.assertEquals(handler.getTotalAmount(0), 55);
	}

	@Test
	public void canDeseriliazeDefaultNBT() {
		handler = new FilteredItemStackHandler(new int[] {1000}, NonNullList.withSize(1, itemStack), new int[] {1});

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Size", 2);
		NBTTagList amounts = new NBTTagList();
		amounts.appendTag(new NBTTagInt(0));

		nbt.setTag("TotalAmounts", amounts);
		nbt.setTag("Items", new NBTTagList());

		handler.deserializeNBT(nbt);

		Assert.assertEquals(handler.getSlots(), 2);
		Assert.assertEquals(handler.getTotalAmount(0), 0);
	}

	//TODO add unit worth

}
*/
