package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModBlocks;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockPedestal extends BlockPedestalPassive {
	public static final PropertyBool ENABLED = PropertyBool.create("enabled");

	public BlockPedestal() {
		super(Names.Blocks.PEDESTAL);
		this.setUnlocalizedName(Names.Blocks.PEDESTAL);
	}

	@Override
	public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
		for(int i = 0; i < 16; i++) {
			list.add(new ItemStack(this, 1, i));
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

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityPedestal();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(ENABLED, (meta & 4) != 0);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, COLOR, ENABLED);
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

	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos p_189540_5_) {
		super.neighborChanged(state, worldIn, pos, neighborBlock, p_189540_5_);

		//noinspection ConstantConditions
		((TileEntityPedestal) worldIn.getTileEntity(pos)).neighborUpdate();
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(
			@Nonnull World world,
			@Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(ENABLED, false);
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
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle + sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D);
					break;
				case EAST:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle - sideOffset, y, zMiddle + randomOffset, 0.0D, 0.0D, 0.0D);
					break;
				case NORTH:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle + randomOffset, y, zMiddle + sideOffset, 0.0D, 0.0D, 0.0D);
					break;
				case SOUTH:
					world.spawnParticle(EnumParticleTypes.REDSTONE, xMiddle + randomOffset, y, zMiddle - sideOffset, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float xOff, float yOff, float zOff) {

		ItemStack heldItem = player.getHeldItem(hand);
		if(world.isRemote)
			return !heldItem.isEmpty() || player.isSneaking();

		if(!(world.getTileEntity(pos) instanceof TileEntityPedestal))
			return false;

		if(heldItem.isEmpty() && !player.isSneaking() && hand == EnumHand.MAIN_HAND && switchClicked(side, xOff, yOff, zOff)) {
			TileEntityPedestal pedestal = (TileEntityPedestal) world.getTileEntity(pos);
			//noinspection ConstantConditions
			pedestal.toggleSwitch();
			return true;
		} else {
			return super.onBlockActivated(world, pos, state, player, hand, side, xOff, yOff, zOff);
		}
	}

	private boolean switchClicked(EnumFacing side, float xOff, float yOff, float zOff) {
		if(yOff < 0.3 || yOff > 0.65)
			return false;
		if(side == EnumFacing.NORTH && (xOff < 0.35 || xOff > 0.65 || zOff != 0.125))
			return false;
		if(side == EnumFacing.SOUTH && (xOff < 0.35 || xOff > 0.65 || zOff != 0.875))
			return false;
		//noinspection SimplifiableIfStatement
		if(side == EnumFacing.WEST && (zOff < 0.35 || zOff > 0.65 || xOff != 0.125))
			return false;
		return !(side == EnumFacing.EAST && (zOff < 0.35 || zOff > 0.65 || xOff != 0.875));

	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntityPedestal pedestal = (TileEntityPedestal) world.getTileEntity(pos);

		PedestalRegistry.unregisterPosition(world.provider.getDimension(), pos);

		//noinspection ConstantConditions
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
