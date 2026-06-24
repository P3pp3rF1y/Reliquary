package reliquary.crafting.alkahestry;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import reliquary.Reliquary;
import reliquary.crafting.AlkahestryDrainRecipe;
import reliquary.crafting.conditions.AlkahestryEnabledCondition;

public class DrainRecipeBuilder {
	private final Item itemResult;
	private final int charge;

	private DrainRecipeBuilder(ItemLike itemResult, int charge) {
		this.itemResult = itemResult.asItem();
		this.charge = charge;
	}

	public static DrainRecipeBuilder drainRecipe(ItemLike result, int charge) {
		return new DrainRecipeBuilder(result, charge);
	}

	public void build(RecipeOutput recipeOutput, ResourceLocation id) {
		ResourceKey<Recipe<?>> fullId = ResourceKey.create(Registries.RECIPE, Reliquary.getRL("alkahestry/drain/" + id.getPath()));
		recipeOutput.withConditions(new AlkahestryEnabledCondition()).accept(fullId, new AlkahestryDrainRecipe(charge, new ItemStack(itemResult)), null);
	}
}
