package xreliquary;

import lib.enderwizards.sandstone.init.ContentHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import xreliquary.lib.Names;

public class CreativeTabXR extends CreativeTabs {

    public CreativeTabXR(int ID, String langName) {
        super(ID, langName);
    }

    @Override
    public Item getTabIconItem() {
        return ContentHandler.getItem(Names.mercy_cross);
    }

}
