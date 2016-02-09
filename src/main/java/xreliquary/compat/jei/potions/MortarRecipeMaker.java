package xreliquary.compat.jei.potions;

import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.compat.jei.alkahestry.AlkahestryChargingRecipeJEI;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MortarRecipeMaker {
    @Nonnull
    public static List<MortarRecipeJEI> getRecipes() {
        ArrayList<MortarRecipeJEI> recipes = new ArrayList<>();

        for(PotionEssence essence : Settings.potionCombinations) {

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
