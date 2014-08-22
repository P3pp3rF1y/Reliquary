package xreliquary.items;

import xreliquary.Reliquary;
import xreliquary.lib.Names;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;

@ContentInit
public class ItemInfernalTear extends ItemBase {

	public ItemInfernalTear() {
		super(Names.infernal_tear);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
	}

}
