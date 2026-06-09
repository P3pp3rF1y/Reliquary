package reliquary.crafting;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Stream;

public class InfernalTearValueHelper {
	private static Map<Item, Integer> clientItemExperiences = Map.of();

	private InfernalTearValueHelper() {
	}

	public static Optional<Integer> getItemExperience(Level level, Item item) {
		return Optional.ofNullable(getItemExperiences(level).get(item));
	}

	public static Map<Item, Integer> getItemExperiences(Level level) {
		return getItemExperiences(level.getRecipeManager());
	}

	public static Map<Item, Integer> getItemExperiences(RecipeManager recipeManager) {
		var recipes = new ArrayList<>(recipeManager.getAllRecipesFor(ModItems.INFERNAL_TEAR_VALUE_TYPE.get()));
		recipes.sort(Comparator.comparing(recipeHolder -> recipeHolder.id().toString()));

		var itemExperiences = new Object2IntLinkedOpenHashMap<Item>();
		for (var recipeHolder : recipes) {
			var recipe = recipeHolder.value();
			for (var stack : recipe.getIngredient().getItems()) {
				itemExperiences.put(stack.getItem(), recipe.getExperiencePoints());
			}
		}

		return itemExperiences;
	}

	public static Map<Item, Integer> getClientItemExperiences() {
		return clientItemExperiences;
	}

	public static void setClientItemExperiences(RecipeManager recipeManager) {
		clientItemExperiences = getItemExperiences(recipeManager);
	}

	public static void clearClientItemExperiences() {
		clientItemExperiences = Map.of();
	}
}
