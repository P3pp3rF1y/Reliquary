package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;

public class Alkahestry {

	public static AlkahestCraftRecipe getCraftingRecipe(ItemStack stack) {
		String key = getStackKey(stack);

		for(String recipeKey : Settings.AlkahestryTome.craftingRecipes.keySet()) {
			AlkahestCraftRecipe recipe = Settings.AlkahestryTome.craftingRecipes.get(recipeKey);
			if(recipe.dictionaryName == null) {
				if (recipeKey.equals(key)) {
					return recipe;
				}
			} else {
				for(ItemStack dict : OreDictionary.getOres(recipe.dictionaryName)) {
					if(OreDictionary.itemMatches(dict, stack, false))
						return recipe;
				}
			}
		}
		return null;
	}

	public static String getStackKey(ItemStack stack) {
		return RegistryHelper.getItemRegistryName(stack.getItem()) + "|" + stack.getMetadata();
	}

	public static AlkahestCraftRecipe getRecipeByDictionaryKey(ItemStack stack) {
		for(AlkahestCraftRecipe recipe : Settings.AlkahestryTome.craftingRecipes.values()) {
			if(recipe.dictionaryName == null)
				continue;
			for(ItemStack dict : OreDictionary.getOres(recipe.dictionaryName)) {
				if(OreDictionary.itemMatches(dict, stack, false))
					return recipe;
			}
		}
		return null;
	}
}
