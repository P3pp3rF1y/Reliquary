package reliquary.crafting;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class InfernalTearValueHelper {
	private InfernalTearValueHelper() {
	}

	public static Optional<Integer> getItemExperience(Level level, Item item) {
		return Optional.ofNullable(getItemExperiences(level).get(item));
	}

	public static Map<Item, Integer> getItemExperiences(Level level) {
		MinecraftServer server = level.getServer();
		return server == null ? Map.of() : getItemExperiences(server.getRecipeManager());
	}

	public static Map<Item, Integer> getItemExperiences(RecipeManager recipeManager) {
		return getItemExperiences(
				recipeManager.getRecipes().stream().filter(recipeHolder -> recipeHolder.value().getType() == ModItems.INFERNAL_TEAR_VALUE_TYPE.get())
						.map(recipeHolder -> (RecipeHolder<InfernalTearValueRecipe>) recipeHolder));
	}

	private static Map<Item, Integer> getItemExperiences(Stream<RecipeHolder<InfernalTearValueRecipe>> recipes) {
		Map<Item, Integer> itemExperiences = new LinkedHashMap<>();

		recipes.sorted(Comparator.comparing(recipeHolder -> recipeHolder.id().toString()))
				.forEach(recipeHolder -> addRecipeEntries(itemExperiences, recipeHolder.value()));

		return itemExperiences;
	}

	private static void addRecipeEntries(Map<Item, Integer> itemExperiences, InfernalTearValueRecipe recipe) {
		for (Holder<Item> itemHolder : recipe.getIngredient().items().toList()) {
			itemExperiences.put(itemHolder.value(), recipe.getExperiencePoints());
		}
	}
}
