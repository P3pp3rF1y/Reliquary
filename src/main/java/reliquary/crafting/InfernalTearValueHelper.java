package reliquary.crafting;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.Comparator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InfernalTearValueHelper {
	private static Map<Item, Integer> clientItemExperiences = Map.of();

	private InfernalTearValueHelper() {
	}

	public static Optional<Integer> getItemExperience(Level level, Item item) {
		return Optional.ofNullable(getItemExperiences(level).get(item));
	}

	public static Map<Item, Integer> getItemExperiences(Level level) {
		if (!(level.recipeAccess() instanceof RecipeManager recipeManager)) {
			return Map.of();
		}

		return getItemExperiences(recipeManager);
	}

	public static Map<Item, Integer> getItemExperiences(RecipeManager recipeManager) {
		List<RecipeHolder<InfernalTearValueRecipe>> recipes = recipeManager.getRecipes().stream()
				.filter(recipeHolder -> recipeHolder.value().getType() == ModItems.INFERNAL_TEAR_VALUE_TYPE.get())
				.map(recipeHolder -> (RecipeHolder<InfernalTearValueRecipe>) recipeHolder)
				.toList();

		return getItemExperiences(recipes);
	}

	public static Map<Item, Integer> getItemExperiences(RecipeMap recipeMap) {
		return getItemExperiences(recipeMap.byType(ModItems.INFERNAL_TEAR_VALUE_TYPE.get()));
	}

	private static Map<Item, Integer> getItemExperiences(Collection<RecipeHolder<InfernalTearValueRecipe>> recipes) {
		Map<Item, Integer> itemExperiences = new LinkedHashMap<>();

		recipes.stream()
				.sorted(Comparator.comparing((RecipeHolder<InfernalTearValueRecipe> recipeHolder) -> recipeHolder.id().toString()))
				.forEach(recipeHolder -> addRecipeEntries(itemExperiences, recipeHolder.value()));

		return itemExperiences;
	}

	public static Map<Item, Integer> getClientItemExperiences() {
		return clientItemExperiences;
	}

	public static void setClientItemExperiences(RecipeMap recipeMap) {
		clientItemExperiences = getItemExperiences(recipeMap);
	}

	public static void clearClientItemExperiences() {
		clientItemExperiences = Map.of();
	}

	private static void addRecipeEntries(Map<Item, Integer> itemExperiences, InfernalTearValueRecipe recipe) {
		for (Holder<Item> itemHolder : recipe.getIngredient().items().toList()) {
			itemExperiences.put(itemHolder.value(), recipe.getExperiencePoints());
		}
	}
}
