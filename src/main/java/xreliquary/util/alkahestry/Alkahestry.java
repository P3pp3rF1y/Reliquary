package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.reference.Settings;
import xreliquary.util.RegistryHelper;

import static xreliquary.util.alkahestry.AlkahestRecipeType.*;

public class Alkahestry {
	public static AlkahestChargeRecipe matchChargeRecipe(ItemStack stack) {
		for(AlkahestChargeRecipe recipe : Settings.AlkahestryTome.chargingRecipes.values()) {
			if(itemMatchesRecipe(stack, recipe))
				return recipe;
		}
		return null;
	}

	public static AlkahestCraftRecipe matchCraftRecipe(ItemStack stack) {
		for(AlkahestCraftRecipe recipe : Settings.AlkahestryTome.craftingRecipes.values()) {
			if(itemMatchesRecipe(stack, recipe))
				return recipe;
		}
		return null;
	}

	private static boolean itemMatchesRecipe(ItemStack stack, AlkahestRecipe recipe) {
		//noinspection ConstantConditions
		String itemName = stack.getItem().getRegistryName().toString();
		int meta = stack.getMetadata();
		if(recipe.type == META) {
			if(recipe.name.equals(itemName) && recipe.meta == meta)
				return true;
		} else if(recipe.type == WILDCARD) {
			if(recipe.name.equals(itemName))
				return true;
		} else {
			for(ItemStack dict : OreDictionary.getOres(recipe.name)) {
				if(OreDictionary.itemMatches(dict, stack, false))
					return true;
			}
		}
		return false;
	}

	public static String getStackKey(ItemStack stack) {
		return RegistryHelper.getItemRegistryName(stack.getItem()) + "|" + stack.getMetadata();
	}
}
