package xreliquary.compat.jei.alkahestry;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

class AlkahestryChargingRecipeJEI extends BlankRecipeWrapper {
	private final ItemStack input;
	private final ItemStack tome;
	private final ItemStack output;

	AlkahestryChargingRecipeJEI(@Nonnull ItemStack input, @Nonnull ItemStack tomeInput, @Nonnull ItemStack output) {
		this.input = input;
		this.tome = tomeInput;
		this.output = output;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, ImmutableList.of(input, tome));
		ingredients.setOutput(ItemStack.class, output);
	}

	public List getInputs() {
		return Collections.singletonList(input);
	}

	public List getOutputs() {
		return Collections.singletonList(output);
	}
}
