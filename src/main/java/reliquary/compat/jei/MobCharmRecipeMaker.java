package reliquary.compat.jei;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import reliquary.Reliquary;
import reliquary.crafting.MobCharmRecipe;
import reliquary.init.ModItems;
import reliquary.item.MobCharmFragmentItem;
import reliquary.item.MobCharmRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MobCharmRecipeMaker {
	private MobCharmRecipeMaker() {
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipes() {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		MobCharmRecipe.REGISTERED_RECIPES.forEach(baseRecipe -> addMobCharmRecipes(recipes, baseRecipe));
		return recipes;
	}

	private static void addMobCharmRecipes(List<RecipeHolder<CraftingRecipe>> recipes, MobCharmRecipe baseRecipe) {

		for (ResourceLocation regName : MobCharmRegistry.getRegisteredNames()) {
			NonNullList<Ingredient> inputs = NonNullList.create();
			baseRecipe.getIngredients().forEach(i -> {
				if (Arrays.stream(i.getItems()).anyMatch(stack -> stack.getItem() instanceof MobCharmFragmentItem)) {
					ItemStack[] items = new ItemStack[i.getItems().length];
					for (int j = 0; j < i.getItems().length; j++) {
						ItemStack item = i.getItems()[j];
						if (item.getItem() instanceof MobCharmFragmentItem) {
							items[j] = ModItems.MOB_CHARM_FRAGMENT.get().getStackFor(regName);
						} else {
							items[j] = item;
						}
					}
					inputs.add(Ingredient.of(items));
				} else {
					inputs.add(i);
				}

			});
			ItemStack output = ModItems.MOB_CHARM.get().getStackFor(regName);

			ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, inputs, Optional.empty());
			ResourceLocation id = Reliquary.getRL("mob_charm_" + regName.toString().replace(':', '_'));
			recipes.add(new RecipeHolder<>(id, new ShapedRecipe("reliquary.mob_charm", CraftingBookCategory.MISC, pattern, output)));
		}
	}
}
