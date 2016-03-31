package xreliquary.items.util;

import net.minecraft.util.math.BlockPos;

public class HarvestRodCache implements IHarvestRodCache {
	@Override
	public BlockPos getStartBlockPos() {
		return null;
	}

	@Override
	public void addBlockToQueue(BlockPos pos) {

	}

	@Override
	public BlockPos getNextBlockInQueue() {
		return null;
	}

	@Override
	public void clearBlockQueue() {

	}

	@Override
	public boolean isQueueEmpty() {
		return false;
	}

	@Override
	public void incrementTimesUsed() {

	}

	@Override
	public int getTimesUsed() {
		return 0;
	}

	@Override
	public void setTimesUsed(int timesUsed) {

	}

	@Override
	public void reset() {

	}
}
