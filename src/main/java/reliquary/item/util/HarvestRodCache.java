package reliquary.item.util;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class HarvestRodCache {
	@Nullable
	private BlockPos startBlockPos;
	private final Queue<BlockPos> blockQueue;

	private HarvestRodCache(Queue<BlockPos> blockQueue, @Nullable BlockPos startBlockPos) {
		this.blockQueue = blockQueue;
		this.startBlockPos = startBlockPos;
	}

	public HarvestRodCache() {
		this(new ArrayDeque<>(), null);
	}

	public void setStartBlockPos(BlockPos pos) {
		startBlockPos = pos;
	}

	@Nullable
	public BlockPos getStartBlockPos() {
		return startBlockPos;
	}

	public void addBlockToQueue(BlockPos pos) {
		blockQueue.add(pos);
	}

	public Optional<BlockPos> getNextBlockInQueue() {
		return Optional.ofNullable(blockQueue.poll());
	}

	public void clearBlockQueue() {
		blockQueue.clear();
	}

	public boolean isQueueEmpty() {
		return blockQueue.isEmpty() || startBlockPos == null;
	}

	public void reset() {
		startBlockPos = null;
		blockQueue.clear();
	}
}
