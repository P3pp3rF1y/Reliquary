package reliquary.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InfernalTearValueHelper {
	private InfernalTearValueHelper() {}

	public static Optional<Integer> getItemExperience(Level level, Item item) {
		return Optional.ofNullable(getItemExperiences(level).get(item));
	}

	public static Map<Item, Integer> getItemExperiences(Level level) {
		return getItemExperiences(level.getRecipeManager());
	}

	public static Map<Item, Integer> getItemExperiences(RecipeManager recipeManager) {
		Map<Item, Integer> itemExperiences = new LinkedHashMap<>();

		recipeManager.getAllRecipesFor(ModItems.INFERNAL_TEAR_VALUE_TYPE.get()).stream()
				.sorted(Comparator.comparing(recipe -> recipe.getId().toString()))
				.forEach(recipe -> addRecipeEntries(itemExperiences, recipe));

		return itemExperiences;
	}

	private static void addRecipeEntries(Map<Item, Integer> itemExperiences, InfernalTearValueRecipe recipe) {
		Ingredient ingredient = recipe.getIngredient();
		for (ItemStack stack : ingredient.getItems()) {
			itemExperiences.put(stack.getItem(), recipe.getExperiencePoints());
		}
	}
}
