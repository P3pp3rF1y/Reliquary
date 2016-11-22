package xreliquary.compat.jei.magazines;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

class MagazineRecipeJEI extends BlankRecipeWrapper implements ICraftingRecipeWrapper {

	private final List<ItemStack> inputs;
	private final ItemStack output;

	MagazineRecipeJEI(@Nonnull List<ItemStack> inputs, @Nonnull ItemStack output) {
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	public List getInputs() {
		return inputs;
	}

	public List getOutputs() {
		return Collections.singletonList(output);
	}
}
