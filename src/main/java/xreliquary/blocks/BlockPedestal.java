package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Names;
import xreliquary.util.pedestal.PedestalRegistry;

import java.util.List;
import java.util.Random;

public class BlockPedestal extends BlockPedestalPassive {
	public static final PropertyBool ENABLED = PropertyBool.create("enabled");

	public BlockPedestal() {
		super();
		this.setUnlocalizedName(Names.pedestal);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < 16; i++) {
			list.add(new ItemStack(ModBlocks.pedestal, 1, i));
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		List<BlockPos> pedestalPositions = PedestalRegistry.getPositionsInRange(worldIn.provider.getDimension(), pos, 160);

		for(BlockPos pedestalPosition : pedestalPositions) {
			TileEntity te = worldIn.getTileEntity(pedestalPosition);
			if(te != null && te instanceof TileEntityPedestal) {
				((TileEntityPedestal) te).updateRedstone();
			}
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPedestal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(ENABLED, (meta & 4) != 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, COLOR, ENABLED});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		if(state.getValue(ENABLED)) {
			i |= 4;
		}

		i |= super.getMetaFromState(state);

		return i;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
		super.neighborChanged(state, worldIn, pos, neighborBlock);

		((TileEntityPedestal) worldIn.getTileEntity(pos)).neighborUpdate();
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(ENABLED, false);
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

		if(state.getValue(ENABLED) && rand.nextInt(3) == 1) {
			EnumFacing enumfacing = state.getValue(FACING);
			double xMiddle = (double) pos.getX() + 0.5D;
			double y = (double) pos.getY() + 4.0D / 16.0D + rand.nextDouble() * 4.0D / 16.0D;
			double zMiddle = (double) pos.getZ() + 0.5D;
			double sideOffset = 0.27D;
			double randomOffset = rand.nextDouble() * 0.3D - 0.15D;

			switch(enumfacing) {
				case WEST:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle + sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D, new int[0]);
					break;
				case EAST:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle - sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D, new int[0]);
					break;
				case NORTH:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle + randomOffset, y, zMiddle + sideOffset, 0.0D, 0.0D, 0.0D, new int[0]);
					break;
				case SOUTH:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle + randomOffset, y, zMiddle - sideOffset, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float xOff, float yOff, float zOff) {
		if(world.isRemote)
			return player.getHeldItem(hand) != null || player.isSneaking();

		if(!(world.getTileEntity(pos) instanceof TileEntityPedestal))
			return false;

		if(heldItem == null && !player.isSneaking() && hand == EnumHand.MAIN_HAND && switchClicked(side, xOff, yOff, zOff)) {
			TileEntityPedestal pedestal = (TileEntityPedestal) world.getTileEntity(pos);
			pedestal.toggleSwitch();
			return true;
		} else {
			return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, xOff, yOff, zOff);
		}
	}

	private boolean switchClicked(EnumFacing side, float xOff, float yOff, float zOff) {
		if(yOff < 0.3 || yOff > 0.65)
			return false;
		if(side == EnumFacing.NORTH && (xOff < 0.35 || xOff > 0.65 || zOff != 0.125))
			return false;
		if(side == EnumFacing.SOUTH && (xOff < 0.35 || xOff > 0.65 || zOff != 0.875))
			return false;
		if(side == EnumFacing.WEST && (zOff < 0.35 || zOff > 0.65 || xOff != 0.125))
			return false;
		return !(side == EnumFacing.EAST && (zOff < 0.35 || zOff > 0.65 || xOff != 0.875));

	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityPedestal pedestal = (TileEntityPedestal) world.getTileEntity(pos);

		PedestalRegistry.unregisterPosition(world.provider.getDimension(), pos);

		pedestal.removeItemsFromLists();

		super.breakBlock(world, pos, state);
	}

	public void setEnabled(World world, BlockPos pos, boolean enabled) {
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(BlockPedestal.ENABLED) != enabled) {
			state = state.withProperty(BlockPedestal.ENABLED, enabled);

			world.setBlockState(pos, state);
		}
	}
}
