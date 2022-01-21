package reliquary.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public class WorldHelper {
	private WorldHelper() {}

	public static Optional<BlockEntity> getBlockEntity(@Nullable BlockGetter world, BlockPos pos) {
		return getBlockEntity(world, pos, BlockEntity.class);
	}

	public static <T> Optional<T> getBlockEntity(@Nullable BlockGetter world, BlockPos pos, Class<T> teClass) {
		if (world == null) {
			return Optional.empty();
		}

		BlockEntity te = world.getBlockEntity(pos);

		if (teClass.isInstance(te)) {
			return Optional.of(teClass.cast(te));
		}

		return Optional.empty();
	}

	private static void notifyBlockUpdate(@Nullable Level world, BlockPos pos) {
		if (world == null) {
			return;
		}

		BlockState state = world.getBlockState(pos);
		world.sendBlockUpdated(pos, state, state, 3);
	}

	public static void notifyBlockUpdate(BlockEntity tile) {
		notifyBlockUpdate(tile.getLevel(), tile.getBlockPos());
	}
}
