package reliquary.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import reliquary.block.tile.PassivePedestalBlockEntity;
import reliquary.item.ICreativeTabItemGenerator;
import reliquary.reference.Config;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class PassivePedestalBlock extends Block implements EntityBlock, ICreativeTabItemGenerator, SimpleWaterloggedBlock {
	static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE = Stream
			.of(box(4, 10, 4, 12, 11, 12), box(3, 0, 3, 13, 1, 13), box(3.5, 9, 4, 4, 10, 12), box(12, 9, 4, 12.5, 10, 12), box(3.5, 9, 3.5, 12.5, 10, 4),
					box(3.5, 9, 12, 12.5, 10, 12.5), box(4, 1, 4, 12, 2, 12), box(4, 8, 4, 12, 10, 12), box(5, 2, 5, 11, 8, 11))
			.reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public PassivePedestalBlock(Properties properties) {
		super(properties.mapColor(MapColor.STONE).strength(1.5F, 2.0F).forceSolidOn());
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (isDisabled()) {
			return;
		}

		itemConsumer.accept(new ItemStack(this));
	}

	protected boolean isDisabled() {
		return Boolean.TRUE.equals(Config.COMMON.disable.disablePassivePedestal.get());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getPlayer() == null) {
			return defaultBlockState();
		}
		return defaultBlockState().setValue(FACING, context.getPlayer().getDirection()).setValue(WATERLOGGED,
				context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction,
			BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (direction != Direction.DOWN && Boolean.TRUE.equals(state.getValue(WATERLOGGED))) {
			scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return player.isCrouching() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}

		return WorldHelper.getBlockEntity(level, pos, PassivePedestalBlockEntity.class).map(pedestal -> {
			if (player.isCrouching()) {
				pedestal.removeAndSpawnItem(level);
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.FAIL;
		}).orElse(InteractionResult.FAIL);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hitResult) {
		if (heldItem.isEmpty()) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		return WorldHelper.getBlockEntity(level, pos, PassivePedestalBlockEntity.class)
				.<InteractionResult>map(pedestal -> InventoryHelper.executeOnItemHandlerAt(level, pos, state, pedestal,
						itemHandler -> InventoryHelper.tryAddingPlayerCurrentItem(player, itemHandler, InteractionHand.MAIN_HAND)
								? InteractionResult.SUCCESS
								: InteractionResult.CONSUME,
						InteractionResult.CONSUME))
				.orElse(InteractionResult.FAIL);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockState result = super.playerWillDestroy(level, pos, state, player);
		WorldHelper.getBlockEntity(level, pos, PassivePedestalBlockEntity.class).ifPresent(pedestal -> pedestal.dropPedestalInventory(level));
		return result;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PassivePedestalBlockEntity(pos, state);
	}

	public static void onRightClicked(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();

		// should only really use the event in case that the player is sneaking with something in offhand and empty mainhand
		if (player.level().isClientSide() || !player.isCrouching() || !player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {
			return;
		}

		Block block = player.level().getBlockState(event.getPos()).getBlock();
		if (!(block instanceof PassivePedestalBlock)) {
			return;
		}

		PassivePedestalBlockEntity pedestal = (PassivePedestalBlockEntity) player.level().getBlockEntity(event.getPos());

		if (pedestal != null) {
			pedestal.removeAndSpawnItem(player.level());
		}

		event.setCanceled(true);
	}
}
