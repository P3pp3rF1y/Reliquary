package xreliquary.items.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.Queue;

public class HarvestRodCache implements IHarvestRodCache {

	private BlockPos startBlockPos;
	private Queue<BlockPos> blockQueue;

	public HarvestRodCache() {
		blockQueue = new ArrayDeque<>();
	}

	@Override
	public void setStartBlockPos(BlockPos pos) {
		startBlockPos = pos;
	}

	@Override
	public BlockPos getStartBlockPos() {
		return startBlockPos;
	}

	@Override
	public void addBlockToQueue(BlockPos pos) {
		blockQueue.add(pos);
	}

	@Override
	public BlockPos getNextBlockInQueue() {
		return blockQueue.poll();
	}

	@Override
	public void clearBlockQueue() {
		blockQueue.clear();
	}

	@Override
	public boolean isQueueEmpty() {
		return blockQueue.isEmpty();
	}

	@Override
	public void reset() {
		startBlockPos = null;
		blockQueue.clear();
	}
}
