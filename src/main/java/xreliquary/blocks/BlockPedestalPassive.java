package xreliquary.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityPedestalPassive;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.util.InventoryHelper;

import java.util.ArrayList;
import java.util.List;

public class BlockPedestalPassive extends BlockBase {
	static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);
	static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private static final AxisAlignedBB PEDESTAL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.6875D, 0.875D);

	public BlockPedestalPassive() {
		super(Material.ROCK, Names.pedestal_passive);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < 16; i++) {
			list.add(new ItemStack(ModBlocks.pedestalPassive, 1, i));
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		EnumDyeColor color = EnumDyeColor.RED;

		if(worldIn.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) worldIn.getTileEntity(pos);

			color = pedestal.getClothColor();
		}

		return state.withProperty(COLOR, color);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		int meta = 0;

		if (world.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

			meta = pedestal.getClothColor().getMetadata();
		}

		return new ItemStack(Item.getItemFromBlock(this), 1, meta);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> list = new ArrayList<>();

		Item item = this.getItemDropped(state,((World) world).rand, fortune);

		int meta = 0;
		if (world.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

			meta = pedestal.getClothColor().getMetadata();
		}

		list.add(new ItemStack(item, 1, meta));

		return list;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 3);
		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, COLOR});
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if(worldIn.getTileEntity(pos) instanceof TileEntityPedestalPassive) {
			TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) worldIn.getTileEntity(pos);

			pedestal.setColor(state.getValue(COLOR));
		}
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return "pickaxe";
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.isRemote)
			return player.getHeldItem(hand) != null || player.isSneaking();

		if(!(world.getTileEntity(pos) instanceof TileEntityPedestalPassive))
			return false;

		TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

		if(heldItem == null) {
			if(player.isSneaking()) {
				pedestal.removeLastPedestalStack();
				return true;
			} else {
				return false;
			}
		} else {
			return InventoryHelper.tryAddingPlayerCurrentItem(player, pedestal, EnumHand.MAIN_HAND);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityPedestalPassive pedestal = (TileEntityPedestalPassive) world.getTileEntity(pos);

		if(pedestal != null) {
			pedestal.dropPedestalInventory();
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool)
	{
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPedestalPassive();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PEDESTAL_AABB;
	}
}
