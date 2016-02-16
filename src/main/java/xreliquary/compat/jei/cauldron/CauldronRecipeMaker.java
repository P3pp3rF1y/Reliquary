package xreliquary.compat.jei.cauldron;


import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.potions.PotionEssence;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CauldronRecipeMaker {
    @Nonnull
    public static List<CauldronRecipeJEI> getRecipes() {
        ArrayList<CauldronRecipeJEI> recipes = new ArrayList<>();

        for(PotionEssence essence : Settings.Potions.uniquePotions) {

            ItemStack input = new ItemStack(ModItems.potionEssence, 1);
            input.setTagCompound(essence.writeToNBT());

            ItemStack output = new ItemStack(ModItems.potion);
            output.setTagCompound(essence.writeToNBT());
            NBTHelper.setBoolean("hasPotion", output, true);


            recipes.add(new CauldronRecipeJEI(input, output));
        }

        return recipes;
    }
}
