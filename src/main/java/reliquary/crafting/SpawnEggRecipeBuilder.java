package reliquary.crafting;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpawnEggRecipeBuilder {
	private final List<Ingredient> ingredients = new ArrayList<>();
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

	private SpawnEggRecipeBuilder() {
	}

	public static SpawnEggRecipeBuilder spawnEggRecipe() {
		return new SpawnEggRecipeBuilder();
	}

	public SpawnEggRecipeBuilder addIngredient(ItemLike itemProvider) {
		ingredients.add(Ingredient.of(itemProvider));
		return this;
	}

	public SpawnEggRecipeBuilder unlockedBy(String name, Criterion<?> criterionIn) {
		criteria.put(name, criterionIn);
		return this;
	}

	public void build(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
		ensureValid(id);
		Advancement.Builder advancementBuilder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(AdvancementRequirements.Strategy.OR);
		criteria.forEach(advancementBuilder::addCriterion);
		recipeOutput.accept(id,
				new FragmentToSpawnEggRecipe(new ShapelessRecipe("", CraftingBookCategory.MISC, new ItemStack(Items.CHICKEN_SPAWN_EGG), ingredients)),
				advancementBuilder.build(id.identifier().withPrefix("recipes/")));
	}

	private void ensureValid(ResourceKey<Recipe<?>> id) {
		if (criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}
}
