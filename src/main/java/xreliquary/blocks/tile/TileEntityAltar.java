package xreliquary.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import xreliquary.blocks.BlockAlkahestryAltar;
import xreliquary.reference.Settings;

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
		if(this.worldObj.isRemote)
			return;
		if(!isActive)
			return;
		int worldTime = (int) (worldObj.getWorldTime() % 24000);
		if(worldTime >= 12000)
			return;
		if(!worldObj.canSeeSky(getPos().add(0, 1, 0)))
			return;
		if(cycleTime > 0) {
			cycleTime--;
			if(cycleTime % 20 == 0) {
				IBlockState blockState = worldObj.getBlockState(this.getPos());
				worldObj.notifyBlockUpdate(this.pos, blockState, blockState, 3);

			}
		} else {
			isActive = false;
			worldObj.setBlockState(getPos().add(0, 1, 0), Blocks.glowstone.getDefaultState());
			BlockAlkahestryAltar.updateAltarBlockState(isActive(), worldObj, getPos());
		}
	}

	public void startCycle() {
		//grabs the cycle time from the configs
		int defaultCycleTime = Settings.Altar.timeInMinutes * 60 * 20;
		int maximumVariance = Settings.Altar.maximumTimeVarianceInMinutes * 60 * 20;
		cycleTime = (int) (defaultCycleTime + (double) maximumVariance * worldObj.rand.nextGaussian());
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

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setShort("cycleTime", (short) cycleTime);
		par1NBTTagCompound.setShort("redstoneCount", (short) redstoneCount);
		par1NBTTagCompound.setBoolean("isActive", isActive);
	}

	public void addRedstone() {
		redstoneCount++;
		if(redstoneCount >= getRedstoneCost()) {
			BlockAlkahestryAltar.updateAltarBlockState(true, worldObj, getPos());
		}

		IBlockState blockState = worldObj.getBlockState(this.getPos());

		worldObj.notifyBlockUpdate(this.pos, blockState, blockState, 3);
	}

	public static int getRedstoneCost() {
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
