package reliquary.items.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilteredItemStack implements INBTSerializable<CompoundTag> {
	private ItemStack filter;

	private ItemStack actualStack = ItemStack.EMPTY;
	private final int amountLimit;
	private final int unitWorth;
	private final boolean canRemove;

	public FilteredItemStack(Item filter, int amountLimit, int unitWorth, boolean canRemove) {
		this(new ItemStack(filter), amountLimit, unitWorth, canRemove);
	}

	FilteredItemStack(int amountLimit, boolean canRemove) {
		this(amountLimit, 1, canRemove);
	}

	FilteredItemStack(ItemStack filter, int amountLimit, boolean canRemove) {
		this(filter, amountLimit, 1, canRemove);
	}

	private FilteredItemStack(ItemStack filter, int amountLimit, int unitWorth, boolean canRemove) {
		this(amountLimit, unitWorth, canRemove);
		this.filter = filter.copy();
		this.filter.setCount(1);
	}

	private FilteredItemStack(int amountLimit, int unitWorth, boolean canRemove) {
		this.amountLimit = amountLimit;
		this.unitWorth = unitWorth;
		this.canRemove = canRemove;
	}

	public ItemStack getFilterStack() {
		return filter;
	}

	public ItemStack getStack() {
		return actualStack;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		ret.put("filter", filter.serializeNBT());
		ret.putInt("amount", actualStack.getCount());
		return ret;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		filter = ItemStack.of(nbt.getCompound("filter"));
		actualStack = filter.copy();
		actualStack.setCount(nbt.getInt("amount"));
	}

	void setCount(int count) {
		if (count == 0) {
			actualStack = ItemStack.EMPTY;
			return;
		}

		if (count > 0 && actualStack.isEmpty()) {
			actualStack = filter.copy();
		}
		actualStack.setCount(count);
	}

	ItemStack insertItem(ItemStack stack, boolean simulate) {
		int remainingTotal = worthToUnits(amountLimit - actualStack.getCount());
		if (remainingTotal <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > remainingTotal;

		if (!simulate) {
			setCount(actualStack.getCount() + unitsToWorth(reachedLimit ? remainingTotal : stack.getCount()));
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - remainingTotal) : ItemStack.EMPTY;
	}

	public boolean isEmpty() {
		return actualStack.isEmpty();
	}

	private int worthToUnits(int valueWorth) {
		return valueWorth / unitWorth;
	}

	private int unitsToWorth(int valueUnits) {
		return unitWorth * valueUnits;
	}

	public int getCount() {
		return actualStack.getCount();
	}

	public boolean canRemove() {
		return canRemove;
	}
}
