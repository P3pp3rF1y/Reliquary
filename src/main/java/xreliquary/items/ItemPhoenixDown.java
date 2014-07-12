package xreliquary.items;

import xreliquary.Reliquary;
import lib.enderwizards.sandstone.init.ContentInit;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

/**
 * Created by Xeno on 5/15/14.
 */
@ContentInit
public class ItemPhoenixDown extends ItemAngelicFeather {

	public ItemPhoenixDown() {
		super(Names.phoenix_down);
		this.setCreativeTab(Reliquary.CREATIVE_TAB);
		this.setMaxStackSize(1);
		canRepair = false;
	}

	// the item does literally nothing else. Honestly I could've done this with
	// metadata. We may give it some powers of its own eventually.
	// everything else this item does occurs in event listeners.

}
