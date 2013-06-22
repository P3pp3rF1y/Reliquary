package xreliquary.items;

import xreliquary.lib.Names;

public class ItemGunsmithCrucible extends ItemXR {
	protected ItemGunsmithCrucible(int par1) {
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
		this.setUnlocalizedName(Names.GUNSMITH_CRUCIBLE_NAME);
	}
}
