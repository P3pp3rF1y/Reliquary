package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import xreliquary.reference.Settings;

public class VoidTearItemStackHandler extends FilteredItemStackHandler {
	public VoidTearItemStackHandler() {
		super(1);
	}

	@Override
	protected int getParentSlotLimit(int parentSlot) {
		return Settings.Items.VoidTear.itemLimit;
	}

	@Override
	protected int getParentSlotUnitWorth(int parentSlot) {
		return 1;
	}

	@Override
	protected boolean isItemStackValidForParentSlot(ItemStack stack, int parentSlot) {
		return parentSlot == 0;
	}
}
