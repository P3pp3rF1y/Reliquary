package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ContentInit
public class BlockWraithNode extends Block {

    public IIcon topIcon;

	public BlockWraithNode() {
		super(Material.rock);
		this.setBlockBounds(0.125F, 0F, 0.125F, 0.875F, 0.750F, 0.875F);
		blockHardness = 1.5F;
		blockResistance = 5.0F;
		this.setBlockName(Names.wraith_node);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(side > 1) {
            return this.blockIcon;
        }
        return topIcon;
    }

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.wraith_node);
        topIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.wraith_node + "_top");
	}

}