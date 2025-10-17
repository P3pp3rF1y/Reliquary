package reliquary.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;
import reliquary.init.ModBlocks;
import reliquary.item.ICreativeTabItemGenerator;
import reliquary.reference.Settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FertileLilyPadBlock extends BushBlock implements ICreativeTabItemGenerator {
	private static final Map<ResourceKey<Level>, Long> currentDimensionTicks = new HashMap<>();
	private static final Map<ResourceKey<Level>, Set<BlockPos>> dimensionPositionsTicked = new HashMap<>();
	private static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.WATER;
	}

	public FertileLilyPadBlock() {
		super(Properties.of().mapColor(MapColor.PLANT).randomTicks());
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		ResourceKey<Level> dimKey = world.dimension();
		if (!currentDimensionTicks.containsKey(dimKey) || currentDimensionTicks.get(dimKey) != world.getGameTime()) {
			currentDimensionTicks.put(dimKey, world.getGameTime());
			dimensionPositionsTicked.put(dimKey, new HashSet<>());
		} else if (dimensionPositionsTicked.computeIfAbsent(dimKey, k -> new HashSet<>()).contains(pos)) {
			return;
		}
		growCropsNearby(world, pos, state);
		dimensionPositionsTicked.computeIfAbsent(dimKey, k -> new HashSet<>()).add(pos);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
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

	@SuppressWarnings("deprecation")
	private void growCropsNearby(ServerLevel world, BlockPos pos, BlockState state) {
		BlockPos.betweenClosed(pos.offset(-tileRange(), -tileRange(), -tileRange()), pos.offset(tileRange(), tileRange(), tileRange())).forEach(cropPos -> {
			if (!world.hasChunkAt(cropPos)) {
				return;
			}

			BlockPos posDiff = cropPos.subtract(pos);
			int distance = Math.max(Math.abs(posDiff.getX()), Math.max(Math.abs(posDiff.getY()), Math.abs(posDiff.getZ())));

			float chanceToGrow = distance <= fullPotencyRange() ? 1F : (1F - (float) (distance - fullPotencyRange()) / (tileRange() - fullPotencyRange() + 1));

			if (world.random.nextFloat() > chanceToGrow) {
				return;
			}

			BlockState cropState = world.getBlockState(cropPos);
			Block cropBlock = cropState.getBlock();

			if (isAllowedCropBlock(cropBlock) && isGrowable(cropBlock)) {
				tickCropBlock(world, cropPos, cropState, distance);
			}
		});
		world.scheduleTick(pos, state.getBlock(), secondsBetweenGrowthTicks() * 20 / getNumberOfPotencySteps());
	}

	private boolean isAllowedCropBlock(Block cropBlock) {
		return cropBlock != ModBlocks.FERTILE_LILY_PAD.get() && cropBlock != Blocks.GRASS_BLOCK && cropBlock != Blocks.GRASS;
	}

	private boolean isGrowable(Block cropBlock) {
		if (cropBlock == Blocks.CHORUS_FLOWER) {
			return true;
		}

		return cropBlock instanceof IPlantable || cropBlock instanceof BonemealableBlock || cropBlock.asItem().builtInRegistryHolder().is(Tags.Items.CROPS);
	}

	private int getNumberOfPotencySteps() {
		return tileRange() - fullPotencyRange() + 1;
	}

	private void tickCropBlock(ServerLevel level, BlockPos cropPos, BlockState cropState, int distance) {
		float randomTickChance = 1F;
		if (cropState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
			randomTickChance = 0.5F;
			if (bonemealableBlock.isValidBonemealTarget(level, cropPos, cropState, false) && level.random.nextFloat() < 0.01F) {
				bonemealableBlock.performBonemeal(level, level.random, cropPos, cropState);
			}
		}
		if (level.random.nextFloat() <= randomTickChance) {
			cropState.randomTick(level, cropPos, level.random);
			if (level.random.nextFloat() < 0.2f) {
				BlockState cropStateAfter = level.getBlockState(cropPos);
				if (cropState != cropStateAfter) {
					level.levelEvent(1505, cropPos, Math.max(tileRange() - distance, 1));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		super.entityInside(state, worldIn, pos, entityIn);
		if (entityIn instanceof Boat) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		FluidState ifluidstate = worldIn.getFluidState(pos);
		return ifluidstate.getType() == Fluids.WATER || state.getBlock() instanceof IceBlock;
	}
}
