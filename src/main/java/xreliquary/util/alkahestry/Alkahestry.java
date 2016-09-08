package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.reference.Settings;

public class Alkahestry {

	public static AlkahestCraftRecipe getDictionaryKey(ItemStack stack) {
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
