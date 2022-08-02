package reliquary.blocks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import reliquary.blocks.AlkahestryAltarBlock;
import reliquary.init.ModBlocks;
import reliquary.reference.Settings;
import reliquary.util.WorldHelper;

public class AlkahestryAltarBlockEntity extends BlockEntityBase {
	private int cycleTime;
	private boolean isActive;
	private int redstoneCount;

	public AlkahestryAltarBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlocks.ALKAHESTRY_ALTAR_TILE_TYPE.get(), pos, state);
		cycleTime = 0;
		redstoneCount = 0;
	}

	public void serverTick(Level level, BlockPos pos) {
		if (level.isClientSide || !isActive || level.isNight() || !level.canSeeSky(pos.above())) {
			return;
		}
		if (cycleTime > 0) {
			cycleTime--;
		} else {
			isActive = false;
			level.setBlockAndUpdate(pos.above(), Blocks.GLOWSTONE.defaultBlockState());
			AlkahestryAltarBlock.updateAltarBlockState(isActive(), level, pos);
		}
	}

	public void startCycle(Level level) {
		//grabs the cycle time from the configs
		int defaultCycleTime = Settings.COMMON.blocks.altar.timeInMinutes.get() * 60 * 20;
		int maximumVariance = Settings.COMMON.blocks.altar.maximumTimeVarianceInMinutes.get() * 60 * 20;
		cycleTime = (int) (defaultCycleTime + (double) maximumVariance * level.random.nextGaussian());
		redstoneCount = 0;
		isActive = true;
	}

	public void stopCycle() {
		isActive = false;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		cycleTime = compound.getShort("cycleTime");
		redstoneCount = compound.getShort("redstoneCount");
		isActive = compound.getBoolean("isActive");
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putShort("cycleTime", (short) cycleTime);
		compound.putShort("redstoneCount", (short) redstoneCount);
		compound.putBoolean("isActive", isActive);
	}

	public void addRedstone(Level level, BlockPos pos) {
		redstoneCount++;
		if (redstoneCount >= getRedstoneCost()) {
			AlkahestryAltarBlock.updateAltarBlockState(true, level, pos);
		}
		WorldHelper.notifyBlockUpdate(this);
	}

	private static int getRedstoneCost() {
		return Settings.COMMON.blocks.altar.redstoneCost.get();
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
