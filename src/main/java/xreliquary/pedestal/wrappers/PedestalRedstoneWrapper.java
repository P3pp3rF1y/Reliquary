package xreliquary.pedestal.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalRedstoneItemWrapper;
import xreliquary.reference.Settings;

import java.util.List;

public class PedestalRedstoneWrapper implements IPedestalRedstoneItemWrapper {
	private boolean powered;

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
	public void updateRedstone( ItemStack stack, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(Settings.COMMON.blocks.pedestal.redstoneWrapperRange.get());
		World world = pedestal.getTheWorld();
		BlockPos thisPos = pedestal.getBlockPos();

		boolean buttonEnabled = pedestal.switchedOn();

		for(BlockPos pos : pedestalsInRange) {
			if(pos.equals(thisPos)) {
				continue;
			}

			IPedestal ped = (IPedestal) world.getTileEntity(pos);
			if(ped != null) {
				if(powered || buttonEnabled || pedestal.getTheWorld().isBlockPowered(pedestal.getBlockPos())) {
					ped.switchOn(thisPos);
				} else {
					ped.switchOff(thisPos);
				}
			}
		}
	}

	@Override
	public void onRemoved( ItemStack stack, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(Settings.COMMON.blocks.pedestal.redstoneWrapperRange.get());
		World world = pedestal.getTheWorld();
		BlockPos thisPos = pedestal.getBlockPos();

		for(BlockPos pos : pedestalsInRange) {
			IPedestal ped = (IPedestal) world.getTileEntity(pos);
			if(ped != null) {
				ped.switchOff(thisPos);
			}
		}
	}
}
