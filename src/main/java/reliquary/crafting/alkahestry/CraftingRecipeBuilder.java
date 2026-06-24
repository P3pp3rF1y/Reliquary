package reliquary.crafting.alkahestry;

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
import reliquary.crafting.AlkahestryCraftingRecipe;
import reliquary.crafting.conditions.AlkahestryEnabledCondition;

public class CraftingRecipeBuilder {
	private final Ingredient ingredient;
	private final int charge;
	private final int resultCount;

	private CraftingRecipeBuilder(Ingredient ingredient, int charge, int resultCount) {
		this.ingredient = ingredient;
		this.charge = charge;
		this.resultCount = resultCount;
	}

	public static CraftingRecipeBuilder craftingRecipe(ItemLike item, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.of(item), charge, resultCount);
	}

	public static CraftingRecipeBuilder craftingRecipe(HolderGetter<Item> items, TagKey<Item> tag, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.of(items.getOrThrow(tag)), charge, resultCount);
	}

	public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
		ResourceKey<Recipe<?>> fullId = ResourceKey.create(Registries.RECIPE, Reliquary.getIdentifier("alkahestry/crafting/" + id.identifier().getPath()));
		recipeOutput.withConditions(new AlkahestryEnabledCondition()).accept(fullId, new AlkahestryCraftingRecipe(ingredient, charge, resultCount), null);
	}
}
