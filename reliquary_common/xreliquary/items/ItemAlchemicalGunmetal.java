package xreliquary.items;

import xreliquary.lib.Names;

public class ItemAlchemicalGunmetal extends ItemXR {
    protected ItemAlchemicalGunmetal(int par1) {
        super(par1);
        this.setMaxStackSize(64);
        this.setMaxDamage(0);
        canRepair = false;
        this.setUnlocalizedName(Names.ALCHEMICAL_GUNMETAL_NAME);
    }
}
