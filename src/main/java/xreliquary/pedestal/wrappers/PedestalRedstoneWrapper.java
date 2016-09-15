package xreliquary.pedestal.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalRedstoneItemWrapper;
import xreliquary.reference.Settings;

import java.util.List;

public class PedestalRedstoneWrapper implements IPedestalRedstoneItemWrapper {
	boolean powered;

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

	public PedestalRedstoneWrapper() {
		this(false);
	}

	public PedestalRedstoneWrapper(boolean powered) {
		this.powered = powered;
	}

	@Override
	public void updateRedstone(ItemStack stack, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(Settings.Pedestal.redstoneWrapperRange);
		World world = pedestal.getTheWorld();
		BlockPos thisPos = pedestal.getBlockPos();

		for(BlockPos pos : pedestalsInRange) {
			IPedestal ped = (IPedestal) world.getTileEntity(pos);
			if(ped != null) {
				if(powered || pedestal.getTheWorld().isBlockPowered(pedestal.getBlockPos())) {
					ped.switchOn(thisPos);
				} else {
					ped.switchOff(thisPos);
				}
			}
		}
	}

	@Override
	public void onRemoved(ItemStack stack, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(Settings.Pedestal.redstoneWrapperRange);
		World world = pedestal.getTheWorld();
		BlockPos thisPos = pedestal.getBlockPos();

		for(BlockPos pos : pedestalsInRange) {
			IPedestal ped = (IPedestal) world.getTileEntity(pos);
			ped.switchOff(thisPos);
		}
	}
}
