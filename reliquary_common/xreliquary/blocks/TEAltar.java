package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TEAltar extends TileEntity {
	private int cycleTime;
	private boolean isActive;
	private int redstoneCount;

	public TEAltar() {
		cycleTime = 0;
		redstoneCount = 0;
		isActive = false;
	}

	@Override
	public void updateEntity() {
		if (!isActive) return;
		int worldTime = (int)(worldObj.getWorldTime() % 24000);
		if (worldTime >= 12000) return;
		if (!worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord)) return;
		if (worldObj.isRemote) return;
		if (cycleTime > 0) {
			cycleTime--;
		} else {
			isActive = false;
			worldObj.setBlock(xCoord, yCoord + 1, zCoord, Block.glowStone.blockID);
			BlockAltar.updateAltarBlockState(isActive(), worldObj, xCoord, yCoord, zCoord);
		}
	}

	public void startCycle() {
		// number of ticks in a minecraft day 20 * 60 * 20 = 24000;
		cycleTime = (int)(24000 + 6000D * worldObj.rand.nextGaussian());
		isActive = true;
		redstoneCount = 0;
		BlockAltar.updateAltarBlockState(isActive(), worldObj, xCoord, yCoord, zCoord);
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
		par1NBTTagCompound.setShort("cycleTime", (short)cycleTime);
		par1NBTTagCompound.setShort("redstoneCount", (short)redstoneCount);
		par1NBTTagCompound.setBoolean("isActive", isActive);
	}

	public void addRedstone() {
		System.out.println("Redstone count was " + redstoneCount + " and is now " + (redstoneCount + 1) + " - machine currently " + (this.isActive() ? "active." : "inactive."));
		redstoneCount++;
		if (redstoneCount > 2) {
			this.startCycle();
		}
	}

	public int getRedstoneCount() {
		return redstoneCount;
	}

	public boolean isActive() {
		return isActive;
	}
}
