package reliquary.compat.jei.mortar;

import net.minecraft.world.item.ItemStack;

import java.util.List;

class MortarRecipeJEI {
	private final List<ItemStack> inputs;
	private final ItemStack output;

	MortarRecipeJEI(List<ItemStack> inputs, ItemStack output) {
		this.inputs = inputs;
		this.output = output;
	}

	public List<ItemStack> getInputs() {
		return inputs;
	}

	public ItemStack getOutput() {
		return output;
	}
}
