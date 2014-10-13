package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemPyromancerStaff extends ItemBase {
    public ItemPyromancerStaff() {
        super(Names.pyromancer_staff);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(513);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public boolean isFull3D(){ return true; }
}
