package reliquary.crafting.alkahestry;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
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

	public static CraftingRecipeBuilder craftingRecipe(TagKey<Item> tag, int charge, int resultCount) {
		return new CraftingRecipeBuilder(Ingredient.of(tag), charge, resultCount);
	}

	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		ResourceLocation fullId = Reliquary.getRL("alkahestry/crafting/" + id.getPath());
		recipeOutput.withConditions(new AlkahestryEnabledCondition()).accept(fullId, new AlkahestryCraftingRecipe(ingredient, charge, resultCount), null);
	}
}
