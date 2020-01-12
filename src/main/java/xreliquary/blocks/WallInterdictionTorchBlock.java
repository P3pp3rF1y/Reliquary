package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.reference.Names;

import java.util.Random;

import static net.minecraft.block.HorizontalBlock.HORIZONTAL_FACING;

public class WallInterdictionTorchBlock extends InterdictionTorchBlock {
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
		double xOffset = (double) ((float) pos.getX() + 0.5F);
		double yOffset = (double) ((float) pos.getY() + 0.7F);
		double zOffset = (double) ((float) pos.getZ() + 0.5F);

		double verticalModifier = 0.22D;
		double horizontalModifier = 0.27D;
		Direction oppositeFacing = state.get(HORIZONTAL_FACING).getOpposite();
		world.addParticle(ParticleTypes.ENTITY_EFFECT, xOffset + horizontalModifier * (double) oppositeFacing.getXOffset(), yOffset + verticalModifier, zOffset + horizontalModifier * (double) oppositeFacing.getZOffset(), 0.0D, 0.0D, 0.0D);
		world.addParticle(ParticleTypes.FLAME, xOffset + horizontalModifier * (double) oppositeFacing.getXOffset(), yOffset + verticalModifier, zOffset + horizontalModifier * (double) oppositeFacing.getZOffset(), 0.0D, 0.0D, 0.0D);
	}
}
