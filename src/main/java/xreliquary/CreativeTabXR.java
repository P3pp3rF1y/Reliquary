package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import xreliquary.init.AbstractionHandler;
import xreliquary.lib.Names;

public class CreativeTabXR extends CreativeTabs {

	public CreativeTabXR(int ID, String langName) {
		super(ID, langName);
	}

	@Override
	public Item getTabIconItem() {
        return AbstractionHandler.getItem(Names.CROSS_NAME);
	}

}
