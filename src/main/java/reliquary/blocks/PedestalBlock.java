package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.blocks.tile.PedestalBlockEntity;
import reliquary.init.ModBlocks;
import reliquary.pedestal.PedestalRegistry;
import reliquary.reference.Settings;
import reliquary.util.BlockEntityHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class PedestalBlock extends PassivePedestalBlock {
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

	public PedestalBlock() {
		super();
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ENABLED);
	}

	@Override
	protected boolean isDisabled() {
		return Boolean.TRUE.equals(Settings.COMMON.disable.disablePedestal.get());
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(level.dimension().getRegistryName(), pos, 160);

		for (BlockPos pedestalPosition : pedestalPositions) {
			WorldHelper.getBlockEntity(level, pedestalPosition, PedestalBlockEntity.class).ifPresent(pedestalBlockEntity -> pedestalBlockEntity.updateRedstone(level));
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PedestalBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide) {
			return null;
		}

		return BlockEntityHelper.createTickerHelper(blockEntityType, ModBlocks.PEDESTAL_TILE_TYPE.get(), (l, p, s, be) -> be.serverTick(l));
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);

		//noinspection ConstantConditions
		((PedestalBlockEntity) level.getBlockEntity(pos)).neighborUpdate(level);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
		if (Boolean.TRUE.equals(state.getValue(ENABLED)) && rand.nextInt(3) == 1) {
			Direction enumfacing = state.getValue(FACING);
			double xMiddle = pos.getX() + 0.5D;
			double y = pos.getY() + 4.0D / 16.0D + rand.nextDouble() * 4.0D / 16.0D;
			double zMiddle = pos.getZ() + 0.5D;
			double sideOffset = 0.27D;
			double randomOffset = rand.nextDouble() * 0.3D - 0.15D;

			switch (enumfacing) {
				case WEST -> world.addParticle(DustParticleOptions.REDSTONE, xMiddle + sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D);
				case EAST -> world.addParticle(DustParticleOptions.REDSTONE, xMiddle - sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D);
				case NORTH -> world.addParticle(DustParticleOptions.REDSTONE, xMiddle + randomOffset, y, zMiddle + sideOffset, 0.0D, 0.0D, 0.0D);
				default -> world.addParticle(DustParticleOptions.REDSTONE, xMiddle + randomOffset, y, zMiddle - sideOffset, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (level.isClientSide) {
			return !heldItem.isEmpty() || player.isCrouching() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
		}

		return WorldHelper.getBlockEntity(level, pos, PedestalBlockEntity.class).map(pedestal -> {
					if (heldItem.isEmpty() && !player.isCrouching() && hand == InteractionHand.MAIN_HAND && hit.getDirection() == state.getValue(FACING).getOpposite()
							&& switchClicked(hit.getDirection(), hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
						pedestal.toggleSwitch(level);
						return InteractionResult.SUCCESS;
					}
					return super.use(state, level, pos, player, hand, hit);
				}
		).orElse(InteractionResult.FAIL);
	}

	private boolean switchClicked(Direction side, Vec3 hitVec) {
		double xOff = hitVec.x();
		double yOff = hitVec.y();
		double zOff = hitVec.z();

		if (yOff < 0.3 || yOff > 0.65) {
			return false;
		}
		return (side.getAxis() == Direction.Axis.Z && xOff >= 0.35 && xOff <= 0.65)
				|| (side.getAxis() == Direction.Axis.X && zOff >= 0.35 && zOff <= 0.65);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) {
			return;
		}
		PedestalRegistry.unregisterPosition(level.dimension().getRegistryName(), pos);
		WorldHelper.getBlockEntity(level, pos, PedestalBlockEntity.class).ifPresent(pedestal -> pedestal.removeAndSpawnItem(level));
		super.onRemove(state, level, pos, newState, isMoving);
	}
}
