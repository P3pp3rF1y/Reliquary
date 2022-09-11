package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
import java.util.stream.Stream;

public class PedestalBlock extends PassivePedestalBlock {
	public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");
	private static final VoxelShape SHAPE = Stream.of(
			Block.box(4, 2, 4, 12, 3, 12),
			Block.box(5, 2, 5, 11, 8, 11),
			Block.box(4, 8, 4, 12, 10, 12),
			Block.box(4, 1, 4, 12, 2, 12),
			Block.box(3.5, 9, 12, 12.5, 10, 12.5),
			Block.box(3.5, 9, 3.5, 12.5, 10, 4),
			Block.box(12, 9, 4, 12.5, 10, 12),
			Block.box(3.5, 9, 4, 4, 10, 12),
			Block.box(3, 0, 3, 13, 1, 13),
			Block.box(4, 10, 4, 12, 11, 12),
			Block.box(4, 3, 4, 12, 4, 12)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

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
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(level.dimension().registry(), pos, 160);

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

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);

		//noinspection ConstantConditions
		((PedestalBlockEntity) level.getBlockEntity(pos)).neighborUpdate(level);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		if (Boolean.TRUE.equals(state.getValue(ENABLED)) && rand.nextInt(2) == 1) {
			Direction dir = Direction.from2DDataValue(rand.nextInt(4));

			double xMiddle = pos.getX() + 0.5D;
			double y = pos.getY() + 1.0D / 16.0D + rand.nextDouble() * 4.0D / 16.0D;
			double zMiddle = pos.getZ() + 0.5D;
			double sideOffset = 4 / 16F + rand.nextDouble() * 2.0D / 16.0D;
			double alongTheSideOffset = rand.nextDouble() * 6 / 16D;

			Vec3i normal = dir.getNormal();

			world.addParticle(DustParticleOptions.REDSTONE, xMiddle + normal.getX() * sideOffset + normal.getZ() * alongTheSideOffset, y, zMiddle + normal.getZ() * sideOffset + normal.getX() * alongTheSideOffset, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (level.isClientSide) {
			return !heldItem.isEmpty() || player.isCrouching() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
		}

		return WorldHelper.getBlockEntity(level, pos, PedestalBlockEntity.class).map(pedestal -> {
					if (heldItem.isEmpty() && !player.isCrouching() && hand == InteractionHand.MAIN_HAND && switchClicked(hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
						pedestal.toggleSwitch(level);
						return InteractionResult.SUCCESS;
					}
					return super.use(state, level, pos, player, hand, hit);
				}
		).orElse(InteractionResult.FAIL);
	}

	private boolean switchClicked(Vec3 hitVec) {
		double xOff = hitVec.x();
		double yOff = hitVec.y();
		double zOff = hitVec.z();

		return yOff >= 1 / 16f && yOff <= 4 / 16f && xOff >= 4 / 16f && xOff <= 12 / 16f && zOff >= 4 / 16f && zOff <= 12 / 16f;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) {
			return;
		}
		PedestalRegistry.unregisterPosition(level.dimension().registry(), pos);
		WorldHelper.getBlockEntity(level, pos, PedestalBlockEntity.class).ifPresent(pedestal -> pedestal.removeAndSpawnItem(level));
		super.onRemove(state, level, pos, newState, isMoving);
	}
}
