package xreliquary.pedestal.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.api.IPedestal;
import xreliquary.api.IPedestalRedstoneItemWrapper;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;
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

	@SuppressWarnings("unused")
	public PedestalRedstoneWrapper() {
		this(false);
	}

	private PedestalRedstoneWrapper(boolean powered) {
		this.powered = powered;
	}

	@Override
	public void updateRedstone(@Nonnull ItemStack stack, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(Settings.Blocks.Pedestal.redstoneWrapperRange);
		World world = pedestal.getTheWorld();
		BlockPos thisPos = pedestal.getBlockPos();

		boolean buttonEnabled = pedestal.switchedOn();

		for(BlockPos pos : pedestalsInRange) {
			if(pos.equals(thisPos))
				continue;

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
	public void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal) {
		List<BlockPos> pedestalsInRange = pedestal.getPedestalsInRange(Settings.Blocks.Pedestal.redstoneWrapperRange);
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
