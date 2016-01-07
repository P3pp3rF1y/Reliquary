package lib.enderwizards.sandstone.blocks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.items.block.ItemBlockMultiple;
import lib.enderwizards.sandstone.mod.ModRegistry;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class SubBlock {

    private final String langName;
    protected IIcon blockIcon;

    protected BlockMultiple parent;
    protected ItemBlockMultiple itemBlock;

    public SubBlock(String langName) {
        this.langName = langName;
    }

    public boolean setParent(BlockMultiple parent) {
        if (this.parent != null)
            return false;
        this.parent = parent;
        return true;
    }

    public boolean setItemBlock(ItemBlockMultiple itemBlock) {
        if (this.itemBlock != null)
            return false;
        this.itemBlock = itemBlock;
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list) {
        this.itemBlock.formatTooltip(null, stack, list);
    }

    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return LanguageHelper.getLocalization(getUnlocalizedName() + ".name");
    }

    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName() {
        return "tile." + langName;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side) {
        return blockIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(ModRegistry.getID(this.parent.getClass().getCanonicalName()) + ":" + this.getUnlocalizedName().substring(5));
    }

}
