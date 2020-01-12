package xreliquary.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ItemHelper {
	private ItemHelper() {}

	public static ItemUseContext getItemUseContext(BlockPos pos, PlayerEntity player) {
		return new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, true));
	}
}
