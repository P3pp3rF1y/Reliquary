package xreliquary.items;

import xreliquary.lib.Names;

public class ItemAlembic extends ItemXR {
	protected ItemAlembic(int par1) {
		super(par1);
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		canRepair = false;
		this.setUnlocalizedName(Names.ALEMBIC_NAME);
	}
}
