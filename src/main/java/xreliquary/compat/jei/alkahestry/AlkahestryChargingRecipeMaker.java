package xreliquary.compat.jei.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlkahestryChargingRecipeMaker {
	@Nonnull
	public static List<AlkahestryChargingRecipeJEI> getRecipes() {
		Map<String, AlkahestChargeRecipe> alkahestryChargingRecipes = Settings.AlkahestryTome.chargingRecipes;

		ArrayList<AlkahestryChargingRecipeJEI> recipes = new ArrayList<>();

		for(AlkahestChargeRecipe recipe : alkahestryChargingRecipes.values()) {
			Object input = null;
			ItemStack inputTome = new ItemStack(ModItems.alkahestryTome, 1, Settings.AlkahestryTome.chargeLimit);

			ItemStack outputTome = new ItemStack(ModItems.alkahestryTome, 1, Settings.AlkahestryTome.chargeLimit - recipe.charge);
			NBTHelper.setInteger("charge", outputTome, recipe.charge);

			if(recipe.dictionaryName != null) {
				if(OreDictionary.getOres(recipe.dictionaryName).size() > 0) {
					input = OreDictionary.getOres(recipe.dictionaryName);
				}
			} else {
				input = recipe.item;
			}
			if(input != null) {
				recipes.add(new AlkahestryChargingRecipeJEI(input, inputTome, outputTome));
			}
		}

		return recipes;
	}
}
