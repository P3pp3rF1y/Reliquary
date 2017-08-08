package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.crafting.IRecipe;
import xreliquary.compat.jei.JEICategory;
import xreliquary.crafting.factories.AlkahestryChargingRecipeFactory.AlkahestryChargingRecipe;
import xreliquary.crafting.factories.AlkahestryCraftingRecipeFactory.AlkahestryCraftingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlkahestryRecipeRegistry {
	private static Map<JEICategory, List<IRecipe>> categoryRecipes = Maps.newHashMap();

	public static void register(JEICategory category, IRecipe recipe) {
		if (!categoryRecipes.keySet().contains(category)) {
			categoryRecipes.put(category, Lists.newArrayList());
		}

		categoryRecipes.get(category).add(recipe);
	}

	@Nonnull
	public static List<IRecipeWrapper> getRecipes(JEICategory category, IStackHelper stackHelper) {
		List<IRecipeWrapper> jeiRecipes = Lists.newArrayList();

		List<IRecipe> recipes = categoryRecipes.get(category);

		for (IRecipe recipe : recipes) {
			switch(category) {
				case ALKAHESTRY_CRAFTING:
					jeiRecipes.add(new AlkahestryCraftingRecipeJEI(stackHelper, (AlkahestryCraftingRecipe) recipe));
					break;
				case ALKAHESTRY_CHARGING:
					jeiRecipes.add(new AlkahestryChargingRecipeJEI(stackHelper, (AlkahestryChargingRecipe) recipe));
			}
		}

		return jeiRecipes;
	}
}
