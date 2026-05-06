package reliquary.crafting;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import reliquary.Reliquary;

public class InfernalTearValueRecipeBuilder {
	private final Ingredient ingredient;
	private final int experiencePoints;

	private InfernalTearValueRecipeBuilder(Ingredient ingredient, int experiencePoints) {
		this.ingredient = ingredient;
		this.experiencePoints = experiencePoints;
	}

	public static InfernalTearValueRecipeBuilder valueRecipe(ItemLike item, int experiencePoints) {
		return new InfernalTearValueRecipeBuilder(Ingredient.of(item), experiencePoints);
	}

	public static InfernalTearValueRecipeBuilder valueRecipe(HolderGetter<Item> items, TagKey<Item> tag, int experiencePoints) {
		return new InfernalTearValueRecipeBuilder(Ingredient.of(items.getOrThrow(tag)), experiencePoints);
	}

	public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
		recipeOutput.accept(id, new InfernalTearValueRecipe(ingredient, experiencePoints), null);
	}

	public void save(RecipeOutput recipeOutput, String name) {
		save(recipeOutput, ResourceKey.create(Registries.RECIPE, Reliquary.getIdentifier("infernal_tear/" + name)));
	}
}
