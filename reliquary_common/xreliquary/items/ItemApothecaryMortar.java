package xreliquary.items;

import xreliquary.lib.Names;

public class ItemApothecaryMortar extends ItemXR {
	protected ItemApothecaryMortar(int par1) {
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
		this.setUnlocalizedName(Names.APOTHECARY_MORTAR_NAME);
	}
}
