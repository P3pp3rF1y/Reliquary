package xreliquary.compat.jei.mortar;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.List;

class MortarRecipeJEI {
	private final List<ItemStack> inputs;
	private final ItemStack output;

	MortarRecipeJEI(List<ItemStack> inputs, ItemStack output) {
		this.inputs = inputs;
		this.output = output;
	}

	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputs(VanillaTypes.ITEM, inputs);
		ingredients.setOutput(VanillaTypes.ITEM, output);
	}
}
