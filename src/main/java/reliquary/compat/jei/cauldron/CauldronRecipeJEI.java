package reliquary.compat.jei.cauldron;

import net.minecraft.world.item.ItemStack;

import java.util.List;

class CauldronRecipeJEI {
	private final List<ItemStack> inputs;

	private final ItemStack output;

	CauldronRecipeJEI(List<ItemStack> inputs, ItemStack output) {
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
