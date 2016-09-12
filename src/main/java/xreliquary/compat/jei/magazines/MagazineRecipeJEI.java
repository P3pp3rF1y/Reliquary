package xreliquary.compat.jei.magazines;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class MagazineRecipeJEI extends BlankRecipeWrapper implements ICraftingRecipeWrapper {
	@Nonnull
	private final List<ItemStack> inputs;

	@Nonnull
	private final ItemStack output;

	@SuppressWarnings("unchecked")
	public MagazineRecipeJEI(@Nonnull List<ItemStack> inputs, @Nonnull ItemStack output) {
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public List getInputs() {
		return inputs;
	}

	@Override
	public List getOutputs() {
		return Collections.singletonList(output);
	}
}
