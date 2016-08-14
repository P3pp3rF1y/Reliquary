package lib.enderwizards.sandstone.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.block.ItemBlockBase;
import lib.enderwizards.sandstone.mod.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBase extends Block implements ICustomItemBlock, ITileEntityProvider {

    protected boolean registerIcon = true;

    public BlockBase(Material material, String langName) {
        super(material);
        this.setBlockName(langName);
        this.setHardness(1.0F);
        this.setResistance(1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        if (!registerIcon)
            return;
        blockIcon = iconRegister.registerIcon(ModRegistry.getID(this.getClass().getCanonicalName()) + ":" + this.getUnlocalizedName().substring(5));
    }

    @Override
    public Class<? extends ItemBlock> getCustomItemBlock() {
        return ItemBlockBase.class;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (world.getTileEntity(x, y, z) != null)
            world.removeTileEntity(x, y, z);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return null;
    }

}