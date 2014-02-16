package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import xreliquary.init.XRItems;

public class CreativeTabXR extends CreativeTabs {

	public CreativeTabXR(int ID, String langName) {
		super(ID, langName);
	}

	@Override
	public int getTabIconItemIndex() {
		return XRItems.mercyCross.itemID;
	}

}
