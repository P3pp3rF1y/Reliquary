package reliquary.block.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import reliquary.block.AlkahestryAltarBlock;
import reliquary.init.ModBlocks;
import reliquary.reference.Config;
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
		if (level.isClientSide() || !isActive || !level.isBrightOutside() || !level.canSeeSky(pos.above())) {
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
		int defaultCycleTime = Config.COMMON.blocks.altar.timeInMinutes.get() * 60 * 20;
		int maximumVariance = Config.COMMON.blocks.altar.maximumTimeVarianceInMinutes.get() * 60 * 20;
		cycleTime = (int) (defaultCycleTime + maximumVariance * level.getRandom().nextGaussian());
		redstoneCount = 0;
		isActive = true;
	}

	public void stopCycle() {
		isActive = false;
	}

	@Override
	protected void loadAdditional(ValueInput in) {
		super.loadAdditional(in);
		cycleTime = in.getShortOr("cycleTime", (short) 0);
		redstoneCount = in.getShortOr("redstoneCount", (short) 0);
		isActive = in.getBooleanOr("isActive", false);
	}

	@Override
	protected void saveAdditional(ValueOutput out) {
		super.saveAdditional(out);
		out.putShort("cycleTime", (short) cycleTime);
		out.putShort("redstoneCount", (short) redstoneCount);
		out.putBoolean("isActive", isActive);
	}

	public void addRedstone(Level level, BlockPos pos) {
		redstoneCount++;
		if (redstoneCount >= getRedstoneCost()) {
			AlkahestryAltarBlock.updateAltarBlockState(true, level, pos);
		}
		WorldHelper.notifyBlockUpdate(this);
	}

	private static int getRedstoneCost() {
		return Config.COMMON.blocks.altar.redstoneCost.get();
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
