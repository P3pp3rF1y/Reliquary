package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@XRInit
public class BlockWraithNode extends Block {

	public BlockWraithNode() {
		super(Material.rock);
		this.setBlockBounds(0.125F, 0.125F, 0.125F, 0.875F, 0.875F, 0.875F);
		blockHardness = 1.5F;
		blockResistance = 5.0F;
		this.setBlockName(Names.WRAITHNODE_NAME);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
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
		blockIcon = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.WRAITHNODE_NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

}