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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityPedestalPassive;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.util.InventoryHelper;

import java.util.List;
import java.util.Random;

public class BlockPedestalPassive extends BlockBase {
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.<EnumDyeColor>create("color", EnumDyeColor.class);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	private static final AxisAlignedBB PEDESTAL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.6875D, 0.875D);

	public BlockPedestalPassive() {
		super(Material.rock, Names.pedestal_passive);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(getDefaultState().withProperty(COLOR, state.getValue(COLOR)));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getHorizontal(meta & 3);

		EnumDyeColor color = EnumDyeColor.byMetadata((meta >> 2) & 15);

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(COLOR, color);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, COLOR});
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing());
	}

	public ItemStack getColorItemBlockStack(int amount, EnumDyeColor color) {
		return new ItemStack(ModBlocks.pedestalPassive, amount, getMetaFromState(getDefaultState().withProperty(COLOR, color)));
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return "pickaxe";
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return -1;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		i |= state.getValue(FACING).getHorizontalIndex();

		i |= (state.getValue(COLOR).getMetadata() << 2);

		return i;
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
