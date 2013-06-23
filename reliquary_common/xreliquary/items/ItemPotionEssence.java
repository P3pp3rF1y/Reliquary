package xreliquary.items;

import xreliquary.lib.Names;

public class ItemPotionEssence extends ItemXR {
	protected ItemPotionEssence(int par1) {
		super(par1);
		this.setMaxStackSize(64);
		this.setMaxDamage(0);
		this.hasSubtypes = true;
		canRepair = false;
		this.setUnlocalizedName(Names.POTION_ESSENCE_NAME);
	}
}
