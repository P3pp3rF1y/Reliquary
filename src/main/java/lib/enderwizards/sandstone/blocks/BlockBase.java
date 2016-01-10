package lib.enderwizards.sandstone.blocks;

import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockBase extends Block implements ICustomItemBlock, ITileEntityProvider {

    protected boolean registerIcon = true;

    public BlockBase(Material material, String langName) {
        super(material);
        this.setUnlocalizedName(langName);
        this.setHardness(1.0F);
        this.setResistance(1.0F);
    }

    @Override
    public Class<? extends ItemBlock> getCustomItemBlock() {
        return ItemBlockBase.class;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        if (world.getTileEntity(pos) != null)
            world.removeTileEntity(pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
    }

}