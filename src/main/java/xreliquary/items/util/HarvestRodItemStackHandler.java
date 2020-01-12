package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.IPlantable;
import xreliquary.reference.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HarvestRodItemStackHandler extends FilteredItemStackHandler {
	public static final int BONEMEAL_SLOT = 0;

	private static List<RemovableStack> getDefaultStacks() {
		List<RemovableStack> stacks = new ArrayList<>();
		stacks.add(new RemovableStack(new FilteredBigItemStack(Items.BONE_MEAL, Settings.COMMON.items.harvestRod.boneMealLimit.get(),
				Settings.COMMON.items.harvestRod.boneMealWorth.get()), false));
		return stacks;
	}

	public HarvestRodItemStackHandler() {
		super(getDefaultStacks());
		this.setDynamicSlotNumber();
	}

	@Override
	protected int getDynamicStackLimit() {
		return Settings.COMMON.items.harvestRod.maxCapacityPerPlantable.get();
	}

	@Override
	protected boolean getBigStackRemovable(int bigStackSlot) {
		return bigStackSlot != BONEMEAL_SLOT;
	}

	@Override
	protected boolean isValidForBigStackSlot(ItemStack stack, int bigStackSlot) {
		if (bigStackSlot == BONEMEAL_SLOT)
			return stack.getItem() == Items.BONE_MEAL;
		return super.isValidForBigStackSlot(stack, bigStackSlot);
	}

	@Override
	protected boolean isValidForDynamicStack(ItemStack stack) {
		return stack.getItem() instanceof IPlantable;
	}

	public int getBoneMealCount() {
		return getTotalAmount(BONEMEAL_SLOT);
	}

	public void setBoneMealCount(int boneMealCount) {
		setTotalAmount(BONEMEAL_SLOT, boneMealCount);
	}

	public Optional<Integer> insertPlantable(ItemStack stack) {
		for (int slot = 0; slot < getSlots(); slot++) {
			if (insertItem(slot, stack, false).isEmpty()) {
				return Optional.of(getBigStackSlot(slot));
			}
		}
		return Optional.empty();
	}

	public void decrementPlantable(int slot) {
		setTotalAmount(slot, getTotalAmount(slot) - 1);
	}

	public int getCountPlantable() {
		return getBigStackSlots() - 1;
	}
}
