package lib.enderwizards.sandstone.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.mod.ModRegistry;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class SubItem {

    private final String langName;
    protected IIcon itemIcon;
    protected ItemMultiple parent;

    public SubItem(String langName) {
        this.langName = langName;
    }

    public boolean setParent(ItemMultiple parent) {
        if (this.parent != null)
            return false;
        this.parent = parent;
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list) {
        this.parent.formatTooltip(null, stack, list);
    }

    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return LanguageHelper.getLocalization(getUnlocalizedName() + ".name");
    }

    @SideOnly(Side.CLIENT)
    public String getUnlocalizedName() {
        return "item." + langName;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return itemIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(ModRegistry.getID(this.parent.getClass().getCanonicalName()) + ":" + this.getUnlocalizedName().substring(5));
    }

}
