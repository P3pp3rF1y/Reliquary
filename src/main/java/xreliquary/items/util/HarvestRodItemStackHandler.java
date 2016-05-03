package xreliquary.items.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.ItemHandlerHelper;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

public class HarvestRodItemStackHandler extends FilteredItemStackHandler {
	public static int BONEMEAL_SLOT = 0;

	public HarvestRodItemStackHandler() {
		super(1);
		this.setDynamicSize(true);
		this.getFilterStacks()[BONEMEAL_SLOT] = new ItemStack(Items.dye, 1, Reference.WHITE_DYE_META);
	}

	@Override
	protected int getParentSlotLimit(int parentSlot) {
		if(parentSlot == BONEMEAL_SLOT)
			return Settings.HarvestRod.boneMealLimit;

		return Settings.HarvestRod.maxCapacityPerPlantable;
	}

	@Override
	protected int getParentSlotUnitWorth(int parentSlot) {
		if(parentSlot == BONEMEAL_SLOT)
			return Settings.HarvestRod.boneMealWorth;

		return 1;
	}

	@Override
	protected boolean getParentSlotRemovable(int parentSlot) {
		return parentSlot != BONEMEAL_SLOT;
	}

	@Override
	protected boolean isItemStackValidForParentSlot(ItemStack stack, int parentSlot) {
		if (parentSlot == BONEMEAL_SLOT)
			return stack.getItem() == Items.dye && stack.getItemDamage() == Reference.WHITE_DYE_META;
		return (stack.getItem() instanceof IPlantable && (filterStacks.length <= parentSlot || ItemHandlerHelper.canItemStacksStack(stack, filterStacks[parentSlot])));
	}
}
