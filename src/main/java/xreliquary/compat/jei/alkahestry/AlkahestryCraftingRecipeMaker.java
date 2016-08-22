package xreliquary.compat.jei.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlkahestryCraftingRecipeMaker {
	@Nonnull
	public static List<AlkahestryCraftingRecipeJEI> getRecipes() {
		Map<String, AlkahestCraftRecipe> alkahestryCraftingRecipes = Settings.AlkahestryTome.craftingRecipes;

		ArrayList<AlkahestryCraftingRecipeJEI> recipes = new ArrayList<>();

		for(AlkahestCraftRecipe recipe : alkahestryCraftingRecipes.values()) {
			Object input = null;
			ItemStack inputTome = new ItemStack(ModItems.alkahestryTome, 1, 0);
			NBTHelper.setInteger("charge", inputTome, Settings.AlkahestryTome.chargeLimit);
			Object output = null;
			ItemStack outputTome = new ItemStack(ModItems.alkahestryTome, 1, recipe.cost);
			NBTHelper.setInteger("charge", outputTome, Settings.AlkahestryTome.chargeLimit - recipe.cost);

			if(recipe.dictionaryName != null) {
				if(OreDictionary.getOres(recipe.dictionaryName).size() > 0) {
					ArrayList<ItemStack> outputOres = new ArrayList<>();

					for(ItemStack ore : OreDictionary.getOres(recipe.dictionaryName)) {
						ItemStack outputOre = ore.copy();
						outputOre.stackSize = recipe.yield + 1;
						outputOres.add(outputOre);
					}
					input = OreDictionary.getOres(recipe.dictionaryName);
					output = outputOres;
				}
			} else {
				input = recipe.item;
				output = new ItemStack(recipe.item.getItem(), recipe.yield + 1, recipe.item.getMetadata());
			}
			if(input != null) {
				recipes.add(new AlkahestryCraftingRecipeJEI(input, inputTome, output, outputTome));
			}
		}

		return recipes;
	}

}
