package xreliquary.blocks;

import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import xreliquary.Reliquary;
import xreliquary.reference.Names;

@ContentInit
public class BlockWraithNode extends Block {
    public BlockWraithNode() {
        super(Material.rock);
        this.setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 0.750F, 0.875F);
        blockHardness = 1.5F;
        blockResistance = 5.0F;
        this.setUnlocalizedName(Names.wraith_node);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() { return false; }

}