package xreliquary.util;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class NoPlayerBlockItemUseContext extends BlockItemUseContext {
	public NoPlayerBlockItemUseContext(World world, BlockPos pos, ItemStack stack, Direction face) {
		super(world, null, Hand.MAIN_HAND, stack, new BlockRayTraceResult(new Vector3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D), face, pos, false));
	}

	@Override
	public Direction getNearestLookingDirection() {
		return getFace();
	}

	@Override
	public Direction[] getNearestLookingDirections() {
		switch (getFace()) {
			case DOWN:
				return new Direction[] {Direction.UP, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.DOWN};
			case SOUTH:
				return new Direction[] {Direction.NORTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
			case NORTH:
				return new Direction[] {Direction.SOUTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
			case EAST:
				return new Direction[] {Direction.WEST, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.UP, Direction.EAST};
			case WEST:
				return new Direction[] {Direction.EAST, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.UP, Direction.WEST};
			case UP:
			default:
				return new Direction[] {Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.UP};
		}
	}
}
