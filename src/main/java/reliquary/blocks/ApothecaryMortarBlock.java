package reliquary.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import reliquary.blocks.tile.ApothecaryMortarBlockEntity;
import reliquary.init.ModItems;
import reliquary.reference.Settings;
import reliquary.util.InventoryHelper;
import reliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ApothecaryMortarBlock extends Block implements EntityBlock {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	private static final VoxelShape MORTAR_SHAPE = Stream.of(
			Block.box(6, 1.5, 6, 10, 2.5, 10),
			Block.box(5, 2.5, 5, 11, 3.5, 11),
			Block.box(5, 0, 5, 11, 1.5, 11),
			Block.box(4, 3.5, 5, 5, 6.5, 11),
			Block.box(11, 3.5, 5, 12, 6.5, 11),
			Block.box(5, 3.5, 11, 11, 6.5, 12),
			Block.box(5, 3.5, 4, 11, 6.5, 5)
	).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public ApothecaryMortarBlock() {
		super(Properties.of(Material.STONE).strength(1.5F, 2.0F));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (Boolean.TRUE.equals(Settings.COMMON.disable.disablePotions.get())) {
			return;
		}
		super.fillItemCategory(group, items);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return MORTAR_SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ApothecaryMortarBlockEntity(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(hand);
		BlockEntity tileEntity = level.getBlockEntity(pos);
		if (!(tileEntity instanceof ApothecaryMortarBlockEntity mortar)) {
			return InteractionResult.FAIL;
		}

		if (heldItem.isEmpty()) {
			if (player.isCrouching()) {
				InventoryHelper.getItemHandlerFrom(mortar).ifPresent(itemHandler -> InventoryHelper.tryRemovingLastStack(itemHandler, level, mortar.getBlockPos()));
				return InteractionResult.SUCCESS;
			}
			boolean done = mortar.usePestle(level);
			level.playSound(null, pos, soundType.getStepSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
			player.swing(hand);
			return done ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
		}

		//if we're in cooldown prevent player from insta inserting essence that they just got from mortar
		if (mortar.isInCooldown(level) && heldItem.getItem() == ModItems.POTION_ESSENCE.get()) {
			return InteractionResult.CONSUME;
		}

		ItemStack stackToAdd = heldItem.copy();
		stackToAdd.setCount(1);

		boolean putItemInSlot = InventoryHelper.getItemHandlerFrom(mortar).map(itemHandler -> {
			if (InventoryHelper.insertIntoInventory(stackToAdd, itemHandler) == 1) {
				heldItem.shrink(1);
				return true;
			}
			return false;
		}).orElse(false);

		if (!putItemInSlot) {
			mortar.usePestle(level);
			level.playSound(null, pos, soundType.getStepSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
			return InteractionResult.CONSUME;
		} else {
			mortar.setChanged();
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getPlayer() == null) {
			return defaultBlockState();
		}
		return defaultBlockState().setValue(FACING, context.getPlayer().getDirection());
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		WorldHelper.getBlockEntity(level, pos, ApothecaryMortarBlockEntity.class).ifPresent(mortar -> mortar.dropItems(level));
		super.onRemove(state, level, pos, newState, isMoving);
	}
}
