package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

class AlkahestryCraftingRecipeJEI extends BlankRecipeWrapper {
	private final ItemStack input;
	private final ItemStack tomeInput;
	private final ItemStack tomeOutput;
	private final ItemStack output;

	AlkahestryCraftingRecipeJEI(@Nonnull ItemStack input, @Nonnull ItemStack tomeInput, @Nonnull ItemStack output, @Nonnull ItemStack tomeOutput) {
		this.input = input;
		this.tomeInput = tomeInput;
		this.output = output;
		this.tomeOutput = tomeOutput;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, ImmutableList.of(input, tomeInput));
		ingredients.setOutputs(ItemStack.class, ImmutableList.of(output, tomeOutput));
	}

	public List getInputs() {
		return ImmutableList.of(input, tomeInput);
	}

	public List getOutputs() {
		return ImmutableList.of(output, tomeOutput);
	}
}
