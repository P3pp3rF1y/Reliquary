package xreliquary.compat.jei.mortar;

import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class MortarRecipeMaker {
    @Nonnull
    public static List<MortarRecipeJEI> getRecipes() {
        ArrayList<MortarRecipeJEI> recipes = new ArrayList<>();

        for(PotionEssence essence : Settings.Potions.potionCombinations) {

            List<ItemStack> inputs = new ArrayList<>();

            for (PotionIngredient ingredient : essence.ingredients) {
                inputs.add(ingredient.item);
            }

            ItemStack output = new ItemStack(ModItems.potionEssence, 1);
            output.setTagCompound(essence.writeToNBT());

            recipes.add(new MortarRecipeJEI(inputs,output));
        }

        return recipes;
    }
}
