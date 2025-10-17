package reliquary.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
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
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.common.Tags;
import reliquary.init.ModBlocks;
import reliquary.item.ICreativeTabItemGenerator;
import reliquary.reference.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class FertileLilyPadBlock extends BushBlock implements ICreativeTabItemGenerator {
	public static final MapCodec<FertileLilyPadBlock> CODEC = simpleCodec(FertileLilyPadBlock::new);
	private static final Map<ResourceKey<Level>, Long> currentDimensionTicks = new HashMap<>();
	private static final Map<ResourceKey<Level>, Set<BlockPos>> dimensionPositionsTicked = new HashMap<>();
	private static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

	public FertileLilyPadBlock() {
		super(Properties.of().mapColor(MapColor.PLANT));
	}

	private FertileLilyPadBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		itemConsumer.accept(new ItemStack(this));
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);

		if (!level.isClientSide()) {
			level.scheduleTick(pos, this, 1);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		ResourceKey<Level> dimKey = level.dimension();
		if (!currentDimensionTicks.containsKey(dimKey) || currentDimensionTicks.get(dimKey) != level.getGameTime()) {
			currentDimensionTicks.put(dimKey, level.getGameTime());
			dimensionPositionsTicked.put(dimKey, new HashSet<>());
		} else if (dimensionPositionsTicked.computeIfAbsent(dimKey, k -> new HashSet<>()).contains(pos)) {
			return;
		}
		growCropsNearby(level, pos, state);
		dimensionPositionsTicked.computeIfAbsent(dimKey, k -> new HashSet<>()).add(pos);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.0F, 0.9F, 0.5F), pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY(), pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0D, 0D, 0D);
	}

	private int secondsBetweenGrowthTicks() {
		return Config.COMMON.blocks.fertileLilypad.secondsBetweenGrowthTicks.get();
	}

	private int tileRange() {
		return Config.COMMON.blocks.fertileLilypad.tileRange.get();
	}

	private int fullPotencyRange() {
		return Config.COMMON.blocks.fertileLilypad.fullPotencyRange.get();
	}

	@SuppressWarnings("deprecation")
	private void growCropsNearby(ServerLevel level, BlockPos pos, BlockState state) {
		BlockPos.betweenClosed(pos.offset(-tileRange(), -tileRange(), -tileRange()), pos.offset(tileRange(), tileRange(), tileRange())).forEach(cropPos -> {
			if (!level.hasChunkAt(cropPos)) {
				return;
			}

			BlockPos posDiff = cropPos.subtract(pos);
			int distance = Math.max(Math.abs(posDiff.getX()), Math.max(Math.abs(posDiff.getY()), Math.abs(posDiff.getZ())));
			float chanceToGrow = distance <= fullPotencyRange() ? 1F : (1F - (float) (distance - fullPotencyRange()) / (tileRange() - fullPotencyRange() + 1));

			if (level.random.nextFloat() > chanceToGrow) {
				return;
			}

			BlockState cropState = level.getBlockState(cropPos);
			Block cropBlock = cropState.getBlock();

			if (isAllowedCropBlock(cropBlock) && isGrowable(cropBlock)) {
				tickCropBlock(level, cropPos, cropState, distance);
			}
		});
		level.scheduleTick(pos, state.getBlock(), secondsBetweenGrowthTicks() * 20 / getNumberOfPotencySteps());
	}

	private boolean isAllowedCropBlock(Block cropBlock) {
		return cropBlock != ModBlocks.FERTILE_LILY_PAD.get() && cropBlock != Blocks.GRASS_BLOCK && cropBlock != Blocks.SHORT_GRASS;
	}

	private boolean isGrowable(Block cropBlock) {
		if (cropBlock == Blocks.CHORUS_FLOWER) {
			return true;
		}

		return cropBlock instanceof SpecialPlantable || cropBlock instanceof BonemealableBlock || cropBlock.asItem().builtInRegistryHolder().is(Tags.Items.CROPS);
	}

	private int getNumberOfPotencySteps() {
		return tileRange() - fullPotencyRange() + 1;
	}

	private void tickCropBlock(ServerLevel level, BlockPos cropPos, BlockState cropState, int distance) {
		float randomTickChance = 1F;
		if (cropState.getBlock() instanceof BonemealableBlock bonemealableBlock) {
			randomTickChance = 0.5F;
			if (bonemealableBlock.isValidBonemealTarget(level, cropPos, cropState) && level.random.nextFloat() < 0.01F) {
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

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn) {
		super.entityInside(state, level, pos, entityIn);
		if (entityIn instanceof Boat) {
			level.destroyBlock(pos, true);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	protected MapCodec<? extends BushBlock> codec() {
		return CODEC;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		FluidState ifluidstate = level.getFluidState(pos);
		return ifluidstate.getType() == Fluids.WATER || state.getBlock() instanceof IceBlock;
	}
}
