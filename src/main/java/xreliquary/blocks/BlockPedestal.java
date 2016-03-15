package xreliquary.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.reference.Names;
import xreliquary.util.InventoryHelper;

public class BlockPedestal extends BlockBase implements ITileEntityProvider {
	public BlockPedestal() {
		super(Material.rock, Names.pedestal);
		this.setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 0.6875F, 0.875F);
		this.setUnlocalizedName(Names.pedestal);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPedestal();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityPedestal pedestal = (TileEntityPedestal) world.getTileEntity(pos);
		if (pedestal == null)
			return false;

		if (player.getCurrentEquippedItem() == null) {
			if (player.isSneaking()) {
				ItemStack copy = pedestal.getStackInSlot(0);
				player.setCurrentItemOrArmor(0, copy);
				pedestal.setInventorySlotContents(0, null);
				return false;
			}
			else {
				return false;
			}
		} else {
			return InventoryHelper.tryAddingPlayerCurrentItem(player, pedestal);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityPedestal pedestal = (TileEntityPedestal) world.getTileEntity(pos);

		if (pedestal != null) {
			net.minecraft.inventory.InventoryHelper.dropInventoryItems(world, pos, pedestal);
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean isFullCube() {return false;}

}
