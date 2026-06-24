package reliquary.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.Optional;

public class WorldHelper {
	private WorldHelper() {
	}

	public static <T> Optional<T> getBlockEntity(@Nullable BlockGetter level, BlockPos pos, Class<T> teClass) {
		if (level == null) {
			return Optional.empty();
		}

		BlockEntity te = level.getBlockEntity(pos);

		if (teClass.isInstance(te)) {
			return Optional.of(teClass.cast(te));
		}

		return Optional.empty();
	}

	private static void notifyBlockUpdate(@Nullable Level level, BlockPos pos) {
		if (level == null) {
			return;
		}

		BlockState state = level.getBlockState(pos);
		level.sendBlockUpdated(pos, state, state, 3);
	}

	public static void notifyBlockUpdate(BlockEntity tile) {
		notifyBlockUpdate(tile.getLevel(), tile.getBlockPos());
	}
}
