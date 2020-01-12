package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import xreliquary.reference.Settings;

import java.util.ArrayList;

public class VoidTearItemStackHandler extends FilteredItemStackHandler {
	private static final int FIRST_SLOT = 0;

	public VoidTearItemStackHandler() {
		super(new ArrayList<>());
	}

	public void setContainedStack(ItemStack stack) {
		setBigStack(FIRST_SLOT, new RemovableStack(new FilteredBigItemStack(stack, Settings.COMMON.items.voidTear.itemLimit.get()), true));
	}

	public void setContainedStackAmount(int amount) {
		setTotalAmount(FIRST_SLOT, amount);
	}

	public ItemStack getTotalAmountStack() {
		FilteredBigItemStack bigStack = getBigStack(FIRST_SLOT);
		return ItemHandlerHelper.copyStackWithSize(bigStack.getOutputStack().copy(), bigStack.getAmount());
	}

	@Override
	protected boolean isValidForBigStackSlot(ItemStack stack, int bigStackSlot) {
		return bigStackSlot == FIRST_SLOT && super.isValidForBigStackSlot(stack, bigStackSlot);
	}

	public int getContainedAmount() {
		return getTotalAmount(FIRST_SLOT);
	}
}
