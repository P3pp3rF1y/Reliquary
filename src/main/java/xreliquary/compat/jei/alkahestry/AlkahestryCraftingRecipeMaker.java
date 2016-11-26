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
			ItemStack inputTome = new ItemStack(ModItems.alkahestryTome, 1, 0);
			NBTHelper.setInteger("charge", inputTome, Settings.AlkahestryTome.chargeLimit);
			ItemStack outputTome = new ItemStack(ModItems.alkahestryTome, 1, recipe.cost);
			NBTHelper.setInteger("charge", outputTome, Settings.AlkahestryTome.chargeLimit - recipe.cost);

			if(recipe.dictionaryName != null) {
				if(OreDictionary.getOres(recipe.dictionaryName).size() > 0) {
					for(ItemStack ore : OreDictionary.getOres(recipe.dictionaryName)) {
						ItemStack outputOre = ore.copy();
						outputOre.setCount(recipe.yield + 1);
						recipes.add(new AlkahestryCraftingRecipeJEI(ore, inputTome, outputOre, outputTome));
					}
				}
			} else {
				ItemStack input = recipe.item;
				ItemStack output = new ItemStack(recipe.item.getItem(), recipe.yield + 1, recipe.item.getMetadata());
				recipes.add(new AlkahestryCraftingRecipeJEI(input, inputTome, output, outputTome));
			}
		}

		return recipes;
	}
}
