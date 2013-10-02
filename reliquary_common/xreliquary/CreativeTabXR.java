package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import xreliquary.items.XRItems;

public class CreativeTabXR extends CreativeTabs {
    public CreativeTabXR(int par1, String par2Str) {
        super(par1, par2Str);
    }

    /**
     * the itemID for the item to be displayed on the tab
     */
    @Override
    public int getTabIconItemIndex() {
        return XRItems.mercyCross.itemID;
    }

}
