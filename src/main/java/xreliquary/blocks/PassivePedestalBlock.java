package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xreliquary.blocks.tile.PedestalPassiveTileEntity;
import xreliquary.reference.Names;
import xreliquary.util.InventoryHelper;
import xreliquary.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PassivePedestalBlock extends BaseBlock {
	static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
	private static final VoxelShape SHAPE = makeCuboidShape(2, 0, 2, 14, 11, 14);
	public static final Set<Block> ALL_PEDESTAL_BLOCKS = new HashSet<>();

	public PassivePedestalBlock(DyeColor dyeColor) {
		this("pedestals/passive/" + dyeColor.getName() + "_" + Names.Blocks.PASSIVE_PEDESTAL);
		ALL_PEDESTAL_BLOCKS.add(this);
	}

	public PassivePedestalBlock(String name) {
		super(name, Properties.create(Material.ROCK).harvestTool(ToolType.PICKAXE));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
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
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (world.isRemote)
			return !heldItem.isEmpty() || player.isSneaking();

		if (!(world.getTileEntity(pos) instanceof PedestalPassiveTileEntity))
			return false;

		Optional<PedestalPassiveTileEntity> pedestal = WorldHelper.getTile(world, pos, PedestalPassiveTileEntity.class);
		if (heldItem.isEmpty()) {
			if (player.isSneaking() && pedestal.isPresent()) {
				//noinspection ConstantConditions
				pedestal.get().removeAndSpawnItem();
				return true;
			} else {
				return false;
			}
		} else {
			return pedestal.map(ped -> InventoryHelper.getItemHandlerFrom(ped)
					.map(itemHandler -> InventoryHelper.tryAddingPlayerCurrentItem(player, itemHandler, Hand.MAIN_HAND)).orElse(false)).orElse(false);
		}
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		WorldHelper.getTile(world, pos, PedestalPassiveTileEntity.class).ifPresent(PedestalPassiveTileEntity::dropPedestalInventory);
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PedestalPassiveTileEntity();
	}

	@SubscribeEvent
	public void onRightClicked(PlayerInteractEvent.RightClickBlock event) {
		PlayerEntity player = event.getEntityPlayer();

		//should only really use the event in case that the player is sneaking with something in offhand and empty mainhand
		if (!player.isSneaking() || !player.getHeldItemMainhand().isEmpty() || player.getHeldItemOffhand().isEmpty())
			return;

		Block block = player.world.getBlockState(event.getPos()).getBlock();
		if (block != this)
			return;

		PedestalPassiveTileEntity pedestal = (PedestalPassiveTileEntity) player.world.getTileEntity(event.getPos());

		if (pedestal != null) {
			pedestal.removeAndSpawnItem();
		}

		event.setCanceled(true);
	}
}
