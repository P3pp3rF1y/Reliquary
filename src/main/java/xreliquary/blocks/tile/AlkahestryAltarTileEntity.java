package xreliquary.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.blocks.AlkahestryAltarBlock;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.InjectionHelper;

public class AlkahestryAltarTileEntity extends TileEntityBase implements ITickableTileEntity {
	@SuppressWarnings("WeakerAccess") // need public here for ObjectHolder to work
	@ObjectHolder(Reference.MOD_ID + ":" + Names.Blocks.ALKAHESTRY_ALTAR)
	public static final TileEntityType<AlkahestryAltarTileEntity> TYPE = InjectionHelper.nullValue();
	private int cycleTime;
	private boolean isActive;
	private int redstoneCount;

	public AlkahestryAltarTileEntity() {
		super(TYPE);
		cycleTime = 0;
		redstoneCount = 0;
	}

	@Override
	public void tick() {
		if (world.isRemote || !isActive || world.getDayTime() >= 12000 || !world.canSeeSky(getPos().up())) {
			return;
		}
		if(cycleTime > 0) {
			cycleTime--;
		} else {
			isActive = false;
			world.setBlockState(getPos().up(), Blocks.GLOWSTONE.getDefaultState());
			AlkahestryAltarBlock.updateAltarBlockState(isActive(), world, getPos());
		}
	}

	public void startCycle() {
		//grabs the cycle time from the configs
		int defaultCycleTime = Settings.COMMON.blocks.altar.timeInMinutes.get() * 60 * 20;
		int maximumVariance = Settings.COMMON.blocks.altar.maximumTimeVarianceInMinutes.get() * 60 * 20;
		cycleTime = (int) (defaultCycleTime + (double) maximumVariance * world.rand.nextGaussian());
		redstoneCount = 0;
		isActive = true;
	}

	public void stopCycle() {
		isActive = false;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		cycleTime = compound.getShort("cycleTime");
		redstoneCount = compound.getShort("redstoneCount");
		isActive = compound.getBoolean("isActive");
	}


	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putShort("cycleTime", (short) cycleTime);
		compound.putShort("redstoneCount", (short) redstoneCount);
		compound.putBoolean("isActive", isActive);

		return compound;
	}

	public void addRedstone() {
		redstoneCount++;
		if(redstoneCount >= getRedstoneCost()) {
			AlkahestryAltarBlock.updateAltarBlockState(true, world, getPos());
		}

		BlockState blockState = world.getBlockState(getPos());

		world.notifyBlockUpdate(pos, blockState, blockState, 3);
	}

	private static int getRedstoneCost() {
		return Settings.COMMON.blocks.altar.redstoneCost.get();
	}

	public int getRedstoneCount() {
		return redstoneCount;
	}

	private boolean isActive() {
		return isActive;
	}

	public int getCycleTime() {
		return cycleTime;
	}
}
