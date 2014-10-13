package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

/**
 * Created by Xeno on 10/11/2014.
 */
@ContentInit
public class ItemRodOfLyssa extends ItemBase {
    public ItemRodOfLyssa() {
        super(Names.rod_of_lyssa);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @Override
    public boolean isFull3D(){ return true; }
}
