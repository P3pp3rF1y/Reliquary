package xreliquary.pedestal.wrappers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalRedstoneItemWrapper;
import xreliquary.reference.Settings;
import xreliquary.util.WorldHelper;

import java.util.List;

public class PedestalRedstoneWrapper implements IPedestalRedstoneItemWrapper {
	private final boolean powered;

	public static class AlwaysOn extends PedestalRedstoneWrapper {
		public AlwaysOn() {
			super(true);
		}
	}

	public static class Toggleable extends PedestalRedstoneWrapper {
		public Toggleable() {
			super(false);
		}
	}

	private PedestalRedstoneWrapper(boolean powered) {
		this.powered = powered;
	}

	@Override
	public void updateRedstone(ItemStack stack, Level level, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(level, Settings.COMMON.blocks.pedestal.redstoneWrapperRange.get());
		BlockPos thisPos = pedestal.getBlockPos();

		boolean buttonEnabled = pedestal.switchedOn();

		for (BlockPos pos : pedestalsInRange) {
			if (pos.equals(thisPos)) {
				continue;
			}

			WorldHelper.getBlockEntity(level, pos, IPedestal.class).ifPresent(pedestalInRange -> {
				if (powered || buttonEnabled || level.hasNeighborSignal(pedestal.getBlockPos())) {
					pedestalInRange.switchOn(level, thisPos);
				} else {
					pedestalInRange.switchOff(level, thisPos);
				}
			});
		}
	}

	@Override
	public void onRemoved(ItemStack stack, Level level, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(level, Settings.COMMON.blocks.pedestal.redstoneWrapperRange.get());
		BlockPos thisPos = pedestal.getBlockPos();

		for (BlockPos pos : pedestalsInRange) {
			IPedestal ped = (IPedestal) level.getBlockEntity(pos);
			if (ped != null) {
				ped.switchOff(level, thisPos);
			}
		}
	}
}
