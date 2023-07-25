package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import reliquary.blocks.tile.PassivePedestalBlockEntity;
import reliquary.items.ICreativeTabItemGenerator;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PassivePedestalBlock extends Block implements EntityBlock, ICreativeTabItemGenerator {
	static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	private static final VoxelShape SHAPE = Stream.of(
			Block.box(4, 10, 4, 12, 11, 12),
			Block.box(3, 0, 3, 13, 1, 13),
			Block.box(3.5, 9, 4, 4, 10, 12),
			Block.box(12, 9, 4, 12.5, 10, 12),
			Block.box(3.5, 9, 3.5, 12.5, 10, 4),
			Block.box(3.5, 9, 12, 12.5, 10, 12.5),
			Block.box(4, 1, 4, 12, 2, 12),
			Block.box(4, 8, 4, 12, 10, 12),
			Block.box(5, 2, 5, 11, 8, 11)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public PassivePedestalBlock() {
		super(Properties.of().mapColor(MapColor.STONE).strength(1.5F, 2.0F));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public void addCreativeTabItems(Consumer<ItemStack> itemConsumer) {
		if (isDisabled()) {
			return;
		}

		itemConsumer.accept(new ItemStack(this));
	}

	protected boolean isDisabled() {
		return Boolean.TRUE.equals(Settings.COMMON.disable.disablePassivePedestal.get());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getPlayer() == null) {
			return defaultBlockState();
		}
		return defaultBlockState().setValue(FACING, context.getPlayer().getDirection());
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (level.isClientSide) {
			return (!heldItem.isEmpty() || player.isCrouching()) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
		}

		if (!(level.getBlockEntity(pos) instanceof PassivePedestalBlockEntity)) {
			return InteractionResult.FAIL;
		}

		Optional<PassivePedestalBlockEntity> pedestal = WorldHelper.getBlockEntity(level, pos, PassivePedestalBlockEntity.class);
		if (heldItem.isEmpty()) {
			if (player.isCrouching() && pedestal.isPresent()) {
				pedestal.get().removeAndSpawnItem(level);
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.FAIL;
			}
		} else {
			return pedestal.map(ped -> InventoryHelper.getItemHandlerFrom(ped)
					.map(itemHandler -> InventoryHelper.tryAddingPlayerCurrentItem(player, itemHandler, InteractionHand.MAIN_HAND) ? InteractionResult.SUCCESS : InteractionResult.CONSUME)
					.orElse(InteractionResult.CONSUME)).orElse(InteractionResult.CONSUME);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		WorldHelper.getBlockEntity(level, pos, PassivePedestalBlockEntity.class).ifPresent(pedestal -> pedestal.dropPedestalInventory(level));
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PassivePedestalBlockEntity(pos, state);
	}

	public static void onRightClicked(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();

		//should only really use the event in case that the player is sneaking with something in offhand and empty mainhand
		if (!player.isCrouching() || !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
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
