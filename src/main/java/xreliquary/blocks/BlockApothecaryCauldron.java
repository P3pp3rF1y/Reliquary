package xreliquary.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockApothecaryCauldron extends BlockBase {

	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
	private static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
	private static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
	private static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	private static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

	public BlockApothecaryCauldron() {
		super(Material.IRON, Names.Blocks.APOTHECARY_CAULDRON, 1.5F, 5.0F);
		setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LEVEL);
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(LEVEL, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LEVEL);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state,
			@Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB mask, @Nonnull List<AxisAlignedBB> list, Entity collisionEntity, boolean p_185477_7_) {
		addCollisionBoxToList(pos, mask, list, AABB_LEGS);
		addCollisionBoxToList(pos, mask, list, AABB_WALL_WEST);
		addCollisionBoxToList(pos, mask, list, AABB_WALL_NORTH);
		addCollisionBoxToList(pos, mask, list, AABB_WALL_EAST);
		addCollisionBoxToList(pos, mask, list, AABB_WALL_SOUTH);
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube? This determines whether
	 * or not to render the shared face of two adjacent blocks and also whether
	 * the player can attach torches, redstone wire, etc to this block.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	/**
	 * Triggered whenever an entity collides with this block (enters into the
	 * block). Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity collidingEntity) {
		if(!world.isRemote) {
			TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
			if(cauldron != null)
				cauldron.handleCollidingEntity(world, pos, collidingEntity);
		}
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float xOff, float yOff, float zOff) {
		if(world.isRemote) {
			return true;
		} else {
			ItemStack heldItem = player.getHeldItem(hand);
			if(heldItem.isEmpty()) {
				return true;
			} else {
				TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);

				if(cauldron != null)
					return cauldron.handleBlockActivation(world, player);
			}
		}
		return true;
	}

	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if(world.rand.nextInt(20) == 1) {
			TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
			if(cauldron != null) {
				cauldron.fillWithRain();
			}
		}
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		// this might destroy the universe
		return ItemBlock.getItemFromBlock(ModBlocks.apothecaryCauldron);
	}

	/**
	 * If this returns true, then comparators facing away from this block will
	 * use the value from getComparatorInputOverride instead of the actual
	 * redstone signal strength.
	 */
	@SuppressWarnings("deprecation")
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is
	 * used instead of the redstone signal strength when this block inputs to a
	 * comparator.
	 */
	@SuppressWarnings("deprecation")
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
		if(cauldron != null) {
			return cauldron.getLiquidLevel();
		}
		return 0;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityCauldron();
	}
}
