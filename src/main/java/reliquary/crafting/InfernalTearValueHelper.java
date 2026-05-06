package reliquary.crafting;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import reliquary.init.ModItems;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class InfernalTearValueHelper {
	private static Map<Item, Integer> clientItemExperiences = Map.of();

	private InfernalTearValueHelper() {
	}

	public static Optional<Integer> getItemExperience(Level level, Item item) {
		return Optional.ofNullable(getItemExperiences(level).get(item));
	}

	public static Map<Item, Integer> getItemExperiences(Level level) {
		return level instanceof ServerLevel serverLevel ? getItemExperiences(serverLevel.getServer().getRecipeManager()) : Map.of();
	}

	public static Map<Item, Integer> getItemExperiences(RecipeManager recipeManager) {
		return getItemExperiences(recipeManager.getRecipes().stream());
	}

	public static Map<Item, Integer> getItemExperiences(RecipeMap recipeMap) {
		return getItemExperiences(recipeMap.values().stream());
	}

	private static Map<Item, Integer> getItemExperiences(Stream<RecipeHolder<?>> recipeStream) {
		Map<Item, Integer> itemExperiences = new LinkedHashMap<>();

		recipeStream
				.filter(recipeHolder -> recipeHolder.value().getType() == ModItems.INFERNAL_TEAR_VALUE_TYPE.get())
				.map(recipeHolder -> (RecipeHolder<InfernalTearValueRecipe>) recipeHolder)
				.sorted(Comparator.comparing(recipeHolder -> recipeHolder.id().location().toString()))
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
