package xreliquary.items.util;

import net.minecraft.util.math.BlockPos;

public interface IHarvestRodCache {
	void setStartBlockPos(BlockPos pos);

	BlockPos getStartBlockPos();

	void addBlockToQueue(BlockPos pos);

	BlockPos getNextBlockInQueue();

	void clearBlockQueue();

	boolean isQueueEmpty();

	void incrementTimesUsed();

	int getTimesUsed();

	void setTimesUsed(int timesUsed);

	void reset();
}
