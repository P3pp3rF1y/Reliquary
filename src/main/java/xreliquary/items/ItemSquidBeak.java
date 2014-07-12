package xreliquary.items;

import lib.enderwizards.sandstone.items.ItemBase;
import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@ContentInit
public class ItemSquidBeak extends ItemBase {

	public ItemSquidBeak() {
		super(Names.squid_beak);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		canRepair = false;
	}
}
