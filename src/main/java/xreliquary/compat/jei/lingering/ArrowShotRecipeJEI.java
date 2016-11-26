package xreliquary.compat.jei.lingering;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

class ArrowShotRecipeJEI extends BlankRecipeWrapper implements IShapedCraftingRecipeWrapper {

	private final List<ItemStack> inputs;
	private final ItemStack output;

	ArrowShotRecipeJEI(@Nonnull List<ItemStack> inputs, @Nonnull ItemStack output) {
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

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 3;
	}
}
