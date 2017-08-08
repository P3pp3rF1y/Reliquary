package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import xreliquary.crafting.factories.AlkahestryCraftingRecipeFactory.AlkahestryCraftingRecipe;
import xreliquary.init.ModItems;

import javax.annotation.Nonnull;
import java.util.List;

class AlkahestryCraftingRecipeJEI implements IRecipeWrapper {
	private final List<List<ItemStack>> inputs;
	private final List<List<ItemStack>> outputs;

	AlkahestryCraftingRecipeJEI(IStackHelper stackHelper, AlkahestryCraftingRecipe recipe) {
		this.inputs = stackHelper.expandRecipeItemStackInputs(recipe.getIngredients());

		ItemStack resultTome = ItemStack.EMPTY;
		List<ItemStack> results = Lists.newArrayList();
		for(List<ItemStack> subTypes : inputs) {
			for(ItemStack stack : subTypes) {
				if(stack.getItem() == ModItems.alkahestryTome) {
					resultTome = stack.copy();
				} else if(!stack.isEmpty()) {
					ItemStack result = stack.copy();
					result.setCount(recipe.getResultCount());
					results.add(result);
				}
			}
		}

		ModItems.alkahestryTome.useCharge(resultTome, recipe.getChargeNeeded());
		this.outputs = ImmutableList.of(results, ImmutableList.of(resultTome));
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputLists(ItemStack.class, outputs);
	}
}
