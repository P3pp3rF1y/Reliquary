package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.crafting.IRecipe;
import xreliquary.crafting.factories.AlkahestryCraftingRecipeFactory.AlkahestryCraftingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlkahestryCraftingRecipeRegistry {
	private static List<IRecipe> recipes = Lists.newArrayList();

	public static void register(IRecipe recipe) {
		recipes.add(recipe);
	}

	@Nonnull
	public static List<AlkahestryCraftingRecipeJEI> getRecipes(IStackHelper stackHelper) {
		List<AlkahestryCraftingRecipeJEI> jeiRecipes = Lists.newArrayList();

		for(IRecipe recipe : recipes) {
			jeiRecipes.add(new AlkahestryCraftingRecipeJEI(stackHelper, (AlkahestryCraftingRecipe) recipe));
		}

		return jeiRecipes;
	}
}
