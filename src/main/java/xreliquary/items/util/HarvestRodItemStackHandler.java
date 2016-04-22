package xreliquary.items.util;

import xreliquary.reference.Settings;

public class HarvestRodItemStackHandler extends FilteredItemStackHandler {
	public static int BONEMEAL_SLOT = 0;

	public HarvestRodItemStackHandler() {
		super(1);
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
}
