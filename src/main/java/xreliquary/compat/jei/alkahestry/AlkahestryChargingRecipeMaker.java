package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.Lists;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import xreliquary.crafting.factories.AlkahestryChargingRecipeFactory.AlkahestryChargingRecipe;
import xreliquary.reference.Settings;

import java.util.List;

public class AlkahestryChargingRecipeMaker {
	public static List<IRecipeWrapper> getRecipes(IStackHelper stackHelper) {
		List<IRecipeWrapper> jeiRecipes = Lists.newArrayList();

		for (AlkahestryChargingRecipe recipe : Settings.AlkahestryTome.chargingRecipes) {
			jeiRecipes.add(new AlkahestryChargingRecipeJEI(stackHelper, recipe));
		}

		return jeiRecipes;
	}
}
