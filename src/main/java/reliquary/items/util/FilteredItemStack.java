package reliquary.items.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilteredItemStack implements INBTSerializable<CompoundTag> {
	private ItemStack filter;

	private ItemStack actualStack = ItemStack.EMPTY;
	private int count = 0;
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
		ret.putInt("amount", count);
		return ret;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		filter = ItemStack.of(nbt.getCompound("filter"));
		count = nbt.getInt("amount");
		actualStack = filter.copy();
		actualStack.setCount(worthToUnits(count));
	}

	void setCount(int count) {
		this.count = count;
		int units = worthToUnits(this.count);
		if (units == 0) {
			actualStack = ItemStack.EMPTY;
			return;
		}

		if (units > 0 && actualStack.isEmpty()) {
			actualStack = filter.copy();
		}
		actualStack.setCount(units);
	}

	ItemStack insertItem(ItemStack stack, boolean simulate) {
		int remainingTotal = worthToUnits(amountLimit - count);
		if (remainingTotal <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > remainingTotal;

		if (!simulate) {
			setCount(count + unitsToWorth(reachedLimit ? remainingTotal : stack.getCount()));
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - remainingTotal) : ItemStack.EMPTY;
	}

	public boolean isEmpty() {
		return count <= 0;
	}

	private int worthToUnits(int valueWorth) {
		return valueWorth / unitWorth;
	}

	int unitsToWorth(int valueUnits) {
		return unitWorth * valueUnits;
	}

	public int getCount() {
		return count;
	}

	public boolean canRemove() {
		return canRemove;
	}
}
