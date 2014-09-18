package xreliquary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import xreliquary.lib.Names;

import javax.print.attribute.standard.Sides;

public class CreativeTabXR extends CreativeTabs {

    public CreativeTabXR(int ID, String langName) {
        super(ID, langName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ContentHandler.getItem(Names.mercy_cross);
    }

}
