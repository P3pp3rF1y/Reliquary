package xreliquary.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import xreliquary.reference.Names;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

import static net.minecraft.block.HorizontalBlock.HORIZONTAL_FACING;

public class WallInterdictionTorchBlock extends InterdictionTorchBlock {
	private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.makeCuboidShape(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D), Direction.SOUTH, Block.makeCuboidShape(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D), Direction.WEST, Block.makeCuboidShape(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D), Direction.EAST, Block.makeCuboidShape(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));
	public WallInterdictionTorchBlock() {
		super(Names.Blocks.WALL_INTERDICTION_TORCH);
		setDefaultState(stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		double xOffset = (float) pos.getX() + 0.5F;
		double yOffset = (float) pos.getY() + 0.7F;
		double zOffset = (float) pos.getZ() + 0.5F;

		double verticalModifier = 0.22D;
		double horizontalModifier = 0.27D;
		Direction oppositeFacing = state.get(HORIZONTAL_FACING).getOpposite();
		world.addParticle(ParticleTypes.ENTITY_EFFECT, xOffset + horizontalModifier * (double) oppositeFacing.getXOffset(), yOffset + verticalModifier, zOffset + horizontalModifier * (double) oppositeFacing.getZOffset(), 0.0D, 0.0D, 0.0D);
		world.addParticle(ParticleTypes.FLAME, xOffset + horizontalModifier * (double) oppositeFacing.getXOffset(), yOffset + verticalModifier, zOffset + horizontalModifier * (double) oppositeFacing.getZOffset(), 0.0D, 0.0D, 0.0D);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPES.get(state.get(HORIZONTAL_FACING));
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction direction = state.get(HORIZONTAL_FACING);
		BlockPos blockpos = pos.offset(direction.getOpposite());
		BlockState blockstate = worldIn.getBlockState(blockpos);
		return blockstate.func_224755_d(worldIn, blockpos, direction);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = getDefaultState();
		IWorldReader iworldreader = context.getWorld();
		BlockPos blockpos = context.getPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for(Direction direction : adirection) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction1 = direction.getOpposite();
				blockstate = blockstate.with(HORIZONTAL_FACING, direction1);
				if (blockstate.isValidPosition(iworldreader, blockpos)) {
					return blockstate;
				}
			}
		}

		return null;
	}
}
