package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.Lists;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import xreliquary.crafting.factories.AlkahestryCraftingRecipeFactory.AlkahestryCraftingRecipe;
import xreliquary.reference.Settings;

import java.util.List;

public class AlkahestryCraftingRecipeMaker {
	public static List<IRecipeWrapper> getRecipes(IStackHelper stackHelper) {
		List<IRecipeWrapper> jeiRecipes = Lists.newArrayList();

		for (AlkahestryCraftingRecipe recipe : Settings.AlkahestryTome.craftingRecipes) {
			jeiRecipes.add(new AlkahestryCraftingRecipeJEI(stackHelper, recipe));
		}

		return jeiRecipes;
	}
}
