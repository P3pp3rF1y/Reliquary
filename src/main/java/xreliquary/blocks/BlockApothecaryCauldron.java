package xreliquary.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityCauldron;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;

import java.util.List;
import java.util.Random;

public class BlockApothecaryCauldron extends BlockBase implements ITileEntityProvider {

	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
	protected static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
	protected static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
	protected static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

	public BlockApothecaryCauldron() {
		super(Material.iron, Names.apothecary_cauldron);
		this.setHardness(1.5F);
		this.setResistance(5.0F);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {LEVEL});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(LEVEL, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LEVEL);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collisionEntity) {
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
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float xOff, float yOff, float zOff) {
		if(world.isRemote) {
			return true;
		} else {
			ItemStack itemstack = player.inventory.getCurrentItem();
			if(itemstack == null) {
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
				cauldron.fillWithRain(world);
			}
		}
	}

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
	public boolean hasComparatorInputOverride() {
		return true;
	}

	/**
	 * If hasComparatorInputOverride returns true, the return value from this is
	 * used instead of the redstone signal strength when this block inputs to a
	 * comparator.
	 */
	public int getComparatorInputOverride(World world, BlockPos pos) {
		TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
		if(cauldron != null) {
			return cauldron.getLiquidLevel();
		}
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int dunnoWhatThisIs) {
		return new TileEntityCauldron();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
}
