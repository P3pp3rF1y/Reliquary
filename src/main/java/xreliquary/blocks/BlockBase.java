package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockBase extends Block {

    public BlockBase(Material material, String langName) {
        super(material);
        this.setUnlocalizedName(langName);
        this.setHardness(1.0F);
        this.setResistance(1.0F);
    }
}