package xreliquary.items.util;

import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface IHarvestRodCache {
	void setStartBlockPos(BlockPos pos);

	BlockPos getStartBlockPos();

	void addBlockToQueue(BlockPos pos);

	Optional<BlockPos> getNextBlockInQueue();

	void clearBlockQueue();

	boolean isQueueEmpty();

	void reset();
}
