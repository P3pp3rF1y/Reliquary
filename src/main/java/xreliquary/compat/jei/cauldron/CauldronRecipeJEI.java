package xreliquary.compat.jei.cauldron;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

import java.util.List;

class CauldronRecipeJEI {
	private final List<ItemStack> inputs;
	private final ItemStack output;

	CauldronRecipeJEI( List<ItemStack> inputs,  ItemStack output) {
		this.inputs = inputs;
		this.output = output;
	}

	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, inputs);
		ingredients.setOutput(VanillaTypes.ITEM, output);
	}
}
