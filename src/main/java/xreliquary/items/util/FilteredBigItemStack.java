package xreliquary.items.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilteredBigItemStack implements INBTSerializable<CompoundTag> {
	private ItemStack filter;

	private int amount;
	private final int amountLimit;
	private final int unitWorth;
	private ItemStack outputStack = ItemStack.EMPTY;
	private ItemStack inputStack = ItemStack.EMPTY;
	public FilteredBigItemStack(Item filter, int amountLimit, int unitWorth) {
		this(new ItemStack(filter), amountLimit, unitWorth);
	}

	FilteredBigItemStack(int amountLimit) {
		this(amountLimit, 1);
	}

	FilteredBigItemStack(ItemStack filter, int amountLimit) {
		this(filter, amountLimit, 1);
	}

	private FilteredBigItemStack(ItemStack filter, int amountLimit, int unitWorth) {
		this(amountLimit, unitWorth);
		this.filter = filter.copy();
		this.filter.setCount(1);
	}

	private FilteredBigItemStack(int amountLimit, int unitWorth) {
		this.amountLimit = amountLimit;
		this.unitWorth = unitWorth;
	}

	public ItemStack getFilterStack() {
		return filter;
	}

	public ItemStack getFullStack() {
		return ItemHandlerHelper.copyStackWithSize(filter, amount);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		ret.put("filter", filter.serializeNBT());
		ret.putInt("amount", amount);
		return ret;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		filter = ItemStack.of(nbt.getCompound("filter"));
		setAmount(nbt.getInt("amount"));
	}

	void setAmount(int newAmount) {
		amount = newAmount;
		outputStack = filter.copy();
		outputStack.setCount(Math.min(amount, filter.getMaxStackSize()));
		int remainingSpace = amountLimit - amount;
		if (remainingSpace < filter.getMaxStackSize()) {
			inputStack = filter.copy();
			inputStack.setCount(filter.getMaxStackSize() - remainingSpace);
		} else {
			inputStack = ItemStack.EMPTY;
		}
	}

	ItemStack insertItem(ItemStack stack, boolean simulate, boolean inputSlot) {
		ItemStack existing = inputSlot ? getInputStack() : getOutputStack();

		int limit = filter.getMaxStackSize() - existing.getCount();
		int remainingTotal = worthToUnits(amountLimit - amount);
		limit = Math.min(limit, remainingTotal);

		if (limit <= 0) {
			return stack;
		}

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate) {
			setAmount(amount + unitsToWorth(reachedLimit ? limit : stack.getCount()));
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	ItemStack getOutputStack() {
		return outputStack;
	}

	ItemStack getInputStack() {
		return inputStack;
	}

	public boolean isEmpty() {
		return amount == 0;
	}

	void markDirty() {
		int inputCount = inputStack.isEmpty() ? 0 : inputStack.getCount();
		int remainingUnits = worthToUnits(amountLimit - amount);
		int outputCount = outputStack.isEmpty() ? 0 : outputStack.getCount();
		int amountUnits = worthToUnits(amount);

		if (inputCount != Math.max(filter.getMaxStackSize() - remainingUnits, 0)) {
			setAmount(amount + unitWorth * (inputCount - Math.max(filter.getMaxStackSize() - remainingUnits, 0)));
		}
		if (outputCount != Math.min(amountUnits, filter.getMaxStackSize())) {
			setAmount(amount + unitsToWorth(outputCount - Math.min(amountUnits, filter.getMaxStackSize())));
		}
	}

	private int worthToUnits(int valueWorth) {
		return valueWorth / unitWorth;
	}

	private int unitsToWorth(int valueUnits) {
		return unitWorth * valueUnits;
	}

	boolean isValid(ItemStack stack) {
		return ItemHandlerHelper.canItemStacksStack(filter, stack);
	}

	void setInputStack(ItemStack inputStack) {
		this.inputStack = inputStack;
	}

	void setOutputStack(ItemStack outputStack) {
		this.outputStack = outputStack;
	}

	public int getAmount() {
		return amount;
	}
}
