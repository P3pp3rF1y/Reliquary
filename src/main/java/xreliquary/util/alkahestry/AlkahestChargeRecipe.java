package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;

public class AlkahestChargeRecipe {
    public ItemStack item = null;
    public int charge = 0;

    public String dictionaryName = null;

    public AlkahestChargeRecipe(ItemStack item, int charge) {
        this.item = item;
        this.charge = charge;
    }
}
