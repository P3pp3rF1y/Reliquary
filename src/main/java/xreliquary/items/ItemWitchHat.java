package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

@ContentInit
public class ItemWitchHat extends ItemBase {

    public ItemWitchHat() {
        super(Names.witch_hat);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return armorType == 0;
    }
}
