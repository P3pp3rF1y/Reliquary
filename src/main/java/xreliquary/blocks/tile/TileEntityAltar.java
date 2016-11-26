package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import xreliquary.blocks.BlockAlkahestryAltar;
import xreliquary.reference.Settings;

import javax.annotation.Nonnull;

public class TileEntityAltar extends TileEntityBase implements ITickable {
	private int cycleTime;
	private boolean isActive;
	private int redstoneCount;

	public TileEntityAltar() {
		cycleTime = 0;
		redstoneCount = 0;
	}

	@Override
	public void update() {
		if(this.world.isRemote)
			return;
		if(!isActive)
			return;
		int worldTime = (int) (world.getWorldTime() % 24000);
		if(worldTime >= 12000)
			return;
		if(!world.canSeeSky(getPos().add(0, 1, 0)))
			return;
		if(cycleTime > 0) {
			cycleTime--;
			if(cycleTime % 20 == 0) {
				IBlockState blockState = world.getBlockState(this.getPos());
				world.notifyBlockUpdate(this.pos, blockState, blockState, 3);

			}
		} else {
			isActive = false;
			world.setBlockState(getPos().add(0, 1, 0), Blocks.GLOWSTONE.getDefaultState());
			BlockAlkahestryAltar.updateAltarBlockState(isActive(), world, getPos());
		}
	}

	public void startCycle() {
		//grabs the cycle time from the configs
		int defaultCycleTime = Settings.Altar.timeInMinutes * 60 * 20;
		int maximumVariance = Settings.Altar.maximumTimeVarianceInMinutes * 60 * 20;
		cycleTime = (int) (defaultCycleTime + (double) maximumVariance * world.rand.nextGaussian());
		redstoneCount = 0;
		isActive = true;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		cycleTime = par1NBTTagCompound.getShort("cycleTime");
		redstoneCount = par1NBTTagCompound.getShort("redstoneCount");
		isActive = par1NBTTagCompound.getBoolean("isActive");
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setShort("cycleTime", (short) cycleTime);
		compound.setShort("redstoneCount", (short) redstoneCount);
		compound.setBoolean("isActive", isActive);

		return compound;
	}

	public void addRedstone() {
		redstoneCount++;
		if(redstoneCount >= getRedstoneCost()) {
			BlockAlkahestryAltar.updateAltarBlockState(true, world, getPos());
		}

		IBlockState blockState = world.getBlockState(this.getPos());

		world.notifyBlockUpdate(this.pos, blockState, blockState, 3);
	}

	private static int getRedstoneCost() {
		return Settings.Altar.redstoneCost;
	}

	public int getRedstoneCount() {
		return redstoneCount;
	}

	public boolean isActive() {
		return isActive;
	}

	public int getCycleTime() {
		return cycleTime;
	}
}
