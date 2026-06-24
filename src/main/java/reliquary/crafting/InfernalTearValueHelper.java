package reliquary.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
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
		return getItemExperiences(recipeManager.getRecipes().stream());
	}

	private static Map<Item, Integer> getItemExperiences(Stream<RecipeHolder<?>> recipeStream) {
		Map<Item, Integer> itemExperiences = new LinkedHashMap<>();

		recipeStream.filter(recipeHolder -> recipeHolder.value().getType() == ModItems.INFERNAL_TEAR_VALUE_TYPE.get())
				.map(recipeHolder -> (RecipeHolder<InfernalTearValueRecipe>) recipeHolder)
				.sorted(Comparator.comparing(recipeHolder -> recipeHolder.id().toString()))
				.forEach(recipeHolder -> addRecipeEntries(itemExperiences, recipeHolder.value()));

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

	private static void addRecipeEntries(Map<Item, Integer> itemExperiences, InfernalTearValueRecipe recipe) {
		for (Item item : Arrays.stream(recipe.getIngredient().getItems()).map(ItemStack::getItem).toList()) {
			itemExperiences.put(item, recipe.getExperiencePoints());
		}
	}
}
