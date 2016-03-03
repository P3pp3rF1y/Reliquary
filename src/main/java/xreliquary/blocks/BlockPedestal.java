package xreliquary.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.reference.Names;

public class BlockPedestal extends BlockBase implements ITileEntityProvider {
	public BlockPedestal() {
		super(Material.rock, Names.pedestal);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityPedestal();
	}
}
