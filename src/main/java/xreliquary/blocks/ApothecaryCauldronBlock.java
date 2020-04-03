package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xreliquary.blocks.tile.ApothecaryCauldronTileEntity;
import xreliquary.reference.Names;

import javax.annotation.Nullable;

public class ApothecaryCauldronBlock extends BaseBlock {

	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
	private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), IBooleanFunction.ONLY_FIRST);

	public ApothecaryCauldronBlock() {
		super(Names.Blocks.APOTHECARY_CAULDRON, Properties.create(Material.IRON).hardnessAndResistance(1.5F, 5.0F).notSolid());
		setDefaultState(stateContainer.getBaseState().with(LEVEL, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return INSIDE;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isRemote) {
			ApothecaryCauldronTileEntity cauldron = (ApothecaryCauldronTileEntity) world.getTileEntity(pos);
			if (cauldron != null) {
				cauldron.handleCollidingEntity(world, pos, entity);
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (world.isRemote) {
			return !heldItem.isEmpty() ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
		} else {
			if (heldItem.isEmpty()) {
				return ActionResultType.CONSUME;
			} else {
				ApothecaryCauldronTileEntity cauldron = (ApothecaryCauldronTileEntity) world.getTileEntity(pos);

				if (cauldron != null) {
					return cauldron.handleBlockActivation(world, player, hand);
				}
			}
		}
		return ActionResultType.CONSUME;
	}

	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if (world.rand.nextInt(20) == 1) {
			ApothecaryCauldronTileEntity cauldron = (ApothecaryCauldronTileEntity) world.getTileEntity(pos);
			if (cauldron != null) {
				cauldron.fillWithRain();
			}
		}
	}

	/**
	 * If this returns true, then comparators facing away from this block will
	 * use the value from getComparatorInputOverride instead of the actual
	 * redstone signal strength.
	 */
	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is
	 * used instead of the redstone signal strength when this block inputs to a
	 * comparator.
	 */
	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		ApothecaryCauldronTileEntity cauldron = (ApothecaryCauldronTileEntity) world.getTileEntity(pos);
		if (cauldron != null) {
			return cauldron.getLiquidLevel();
		}
		return 0;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ApothecaryCauldronTileEntity();
	}
}
