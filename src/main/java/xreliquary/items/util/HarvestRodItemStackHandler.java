package xreliquary.items.util;

import net.minecraft.block.IGrowable;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
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
		setDynamicSlotNumber();
	}

	@Override
	protected int getDynamicStackLimit() {
		return Settings.COMMON.items.harvestRod.maxCapacityPerPlantable.get();
	}

	@Override
	protected boolean isValidForBigStackSlot(ItemStack stack, int bigStackSlot) {
		if (bigStackSlot == BONEMEAL_SLOT) {
			return stack.getItem() == Items.BONE_MEAL;
		}
		return super.isValidForBigStackSlot(stack, bigStackSlot);
	}

	@Override
	protected boolean isValidForDynamicStack(ItemStack stack) {
		return isPlantable(stack);
	}

	private boolean isPlantable(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof IPlantable || (item instanceof BlockNamedItem && ((BlockNamedItem) item).getBlock() instanceof IGrowable);
	}

	public int getBoneMealCount() {
		return getTotalAmount(BONEMEAL_SLOT);
	}

	public void setBoneMealCount(int boneMealCount) {
		setTotalAmount(BONEMEAL_SLOT, boneMealCount);
	}

	public Optional<PlantableSlotInserted> insertPlantable(ItemStack stack) {
		for (int slot = 1; slot < getSlots(); slot++) {
			ItemStack result = insertItem(slot, stack, false);
			if (result.getCount() < stack.getCount()) {
				return Optional.of(new PlantableSlotInserted(getBigStackSlot(slot), stack.getCount() - result.getCount()));
			}
		}
		return Optional.empty();
	}

	public static class PlantableSlotInserted {
		private final int slot;
		private final int countInserted;

		public PlantableSlotInserted(int slot, int countInserted) {
			this.slot = slot;
			this.countInserted = countInserted;
		}

		public int getSlot() {
			return slot;
		}

		public int getCountInserted() {
			return countInserted;
		}
	}

	public int getCountPlantable() {
		return getBigStackSlots() - 1;
	}
}
