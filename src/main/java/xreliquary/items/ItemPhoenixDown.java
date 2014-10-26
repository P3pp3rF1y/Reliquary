package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

@ContentInit
public class ItemPhoenixDown extends ItemAngelicFeather {

    public ItemPhoenixDown() {
        super(Names.phoenix_down);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(1);
        canRepair = false;
    }
}
