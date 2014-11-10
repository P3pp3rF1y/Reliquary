package xreliquary.util.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import xreliquary.items.ItemPotionEssence;

/**
 * Created by Xeno on 11/8/2014.
 */
public class XRPotionHelper {

    public static boolean isItemEssence(ItemStack ist) {
        // essence not quite a thing just yet.
        return ist.getItem() instanceof ItemPotionEssence;
    }

    public static boolean isItemIngredient(ItemStack ist) {
        for (PotionIngredient ingredient : PotionMap.getIngredients()) {
            if (ingredient.itemName.equals(ist.getItem().getUnlocalizedNameInefficiently(ist))) {
                return true;
            }
        }
        return false;
    }

    public static int getPotionIdByName(String name) {
        for (Potion potion : Potion.potionTypes) {
            if (potion == null)
                continue;
            if (potion.getName().equals("potion." + name))
                return potion.getId();
        }
        return 0;
    }
}
