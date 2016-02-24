package xreliquary.util.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import xreliquary.items.ItemPotionEssence;
import xreliquary.reference.Settings;

/**
 * Created by Xeno on 11/8/2014.
 */
public class XRPotionHelper {

    public static boolean isItemEssence(ItemStack ist) {
        // essence not quite a thing just yet.
        return ist.getItem() instanceof ItemPotionEssence;
    }

    public static boolean isItemIngredient(ItemStack ist) {
        for (PotionIngredient ingredient : Settings.Potions.potionMap) {
            if (ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata()) {
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
    public static PotionIngredient getIngredient(ItemStack ist) {
        if (ist.getItem() instanceof ItemPotionEssence) {
            return new PotionEssence(ist.getTagCompound());
        }
        for (PotionIngredient ingredient : Settings.Potions.potionMap) {
            if (ingredient.item.getItem().getRegistryName().equals(ist.getItem().getRegistryName()) && ingredient.item.getMetadata() == ist.getMetadata())
                return ingredient;
        }
        return null;
    }
}
