package xreliquary.items;


import xreliquary.Reliquary;
import xreliquary.init.XRInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

@XRInit
public class ItemEnderStaff extends ItemBase {

    public ItemEnderStaff() {
        super(Reference.MOD_ID, Names.ender_staff);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }

}
