package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xreliquary.blocks.tile.ApothecaryMortarTileEntity;
import xreliquary.init.ModItems;
import xreliquary.util.InventoryHelper;
import xreliquary.util.WorldHelper;

import javax.annotation.Nullable;

public class ApothecaryMortarBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	private static final VoxelShape MORTAR_SHAPE = makeCuboidShape(4D, 0D, 4D, 12D, 7D, 12D);

	public ApothecaryMortarBlock() {
		super(Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 2.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return MORTAR_SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ApothecaryMortarTileEntity();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);
		TileEntity tileEntity = world.getTileEntity(pos);
		if (!(tileEntity instanceof ApothecaryMortarTileEntity)) {
			return ActionResultType.FAIL;
		}
		ApothecaryMortarTileEntity mortar = (ApothecaryMortarTileEntity) tileEntity;

		if (heldItem.isEmpty()) {
			if (player.isCrouching()) {
				InventoryHelper.getItemHandlerFrom(mortar).ifPresent(itemHandler -> InventoryHelper.tryRemovingLastStack(itemHandler, world, mortar.getPos()));
				return ActionResultType.SUCCESS;
			}
			boolean done = mortar.usePestle();
			world.playSound(null, pos, soundType.getStepSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
			player.swingArm(hand);
			return done ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
		}

		//if we're in cooldown prevent player from insta inserting essence that they just got from mortar
		if (mortar.isInCooldown() && heldItem.getItem() == ModItems.POTION_ESSENCE.get()) {
			return ActionResultType.CONSUME;
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
			mortar.usePestle();
			world.playSound(null, pos, soundType.getStepSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
			return ActionResultType.CONSUME;
		} else {
			mortar.markDirty();
		}
		return ActionResultType.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		if (context.getPlayer() == null) {
			return getDefaultState();
		}
		return getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing());
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		WorldHelper.getTile(world, pos, ApothecaryMortarTileEntity.class).ifPresent(ApothecaryMortarTileEntity::dropItems);
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
