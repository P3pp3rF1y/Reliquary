package xreliquary.items;

import xreliquary.lib.Names;

public class ItemStackedEssence extends ItemXR {
	protected ItemStackedEssence(int par1) {
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
		this.setUnlocalizedName(Names.STACKED_ESSENCE_NAME);
	}
}
