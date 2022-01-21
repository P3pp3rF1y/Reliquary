package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import reliquary.reference.Settings;

import java.util.Random;

public class FertileLilyPadBlock extends BushBlock {
	private static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.WATER;
	}

	public FertileLilyPadBlock() {
		super(Properties.of(Material.PLANT).randomTicks());
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
		growCropsNearby(world, pos, state);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
		world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY(), pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.0D, 0.9D, 0.5D);
	}

	private int secondsBetweenGrowthTicks() {
		return Settings.COMMON.blocks.fertileLilypad.secondsBetweenGrowthTicks.get();
	}

	private int tileRange() {
		return Settings.COMMON.blocks.fertileLilypad.tileRange.get();
	}

	private int fullPotencyRange() {
		return Settings.COMMON.blocks.fertileLilypad.fullPotencyRange.get();
	}

	private void growCropsNearby(ServerLevel world, BlockPos pos, BlockState state) {
		BlockPos.betweenClosed(pos.offset(-tileRange(), -1, -tileRange()), pos.offset(tileRange(), tileRange(), tileRange())).forEach(cropPos -> {
			if (!world.hasChunkAt(cropPos)) {
				return;
			}
			BlockState cropState = world.getBlockState(cropPos);
			Block cropBlock = cropState.getBlock();

			if ((cropBlock instanceof IPlantable || cropBlock instanceof BonemealableBlock) && !(cropBlock instanceof FertileLilyPadBlock)) {
				double distance = Math.sqrt(cropPos.distSqr(pos));
				tickCropBlock(world, cropPos, cropState, cropBlock, distance);
			}
		});
		world.scheduleTick(pos, state.getBlock(), secondsBetweenGrowthTicks() * 20);
	}

	private void tickCropBlock(ServerLevel world, BlockPos cropPos, BlockState cropState, Block cropBlock, double distance) {
		distance -= fullPotencyRange();
		distance = Math.max(1D, distance);
		double distanceCoefficient = 1D - (distance / tileRange());

		//it schedules the next tick.
		world.scheduleTick(cropPos, cropBlock, (int) (distanceCoefficient * secondsBetweenGrowthTicks() * 20F));
		cropBlock.randomTick(cropState, world, cropPos, world.random);
		world.levelEvent(2005, cropPos, Math.max((int) (tileRange() - distance), 1));
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		super.entityInside(state, worldIn, pos, entityIn);
		if (entityIn instanceof Boat) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		FluidState ifluidstate = worldIn.getFluidState(pos);
		return ifluidstate.getType() == Fluids.WATER || state.getMaterial() == Material.ICE;
	}
}
