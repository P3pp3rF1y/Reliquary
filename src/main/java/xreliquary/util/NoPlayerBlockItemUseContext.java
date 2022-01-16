package xreliquary.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class NoPlayerBlockItemUseContext extends BlockPlaceContext {
	public NoPlayerBlockItemUseContext(Level world, BlockPos pos, ItemStack stack, Direction face) {
		super(world, null, InteractionHand.MAIN_HAND, stack, new BlockHitResult(new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D), face, pos, false));
	}

	@Override
	public Direction getNearestLookingDirection() {
		return getClickedFace();
	}

	@Override
	public Direction[] getNearestLookingDirections() {
		return switch (getClickedFace()) {
			case DOWN -> new Direction[] {Direction.UP, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.DOWN};
			case SOUTH -> new Direction[] {Direction.NORTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
			case NORTH -> new Direction[] {Direction.SOUTH, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
			case EAST -> new Direction[] {Direction.WEST, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.UP, Direction.EAST};
			case WEST -> new Direction[] {Direction.EAST, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.UP, Direction.WEST};
			case UP -> new Direction[] {Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.UP};
		};
	}
}
