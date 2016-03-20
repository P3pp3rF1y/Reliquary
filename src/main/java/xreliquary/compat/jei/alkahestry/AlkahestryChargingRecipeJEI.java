package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class AlkahestryChargingRecipeJEI extends BlankRecipeWrapper {
	@Nonnull
	private final List<Object> inputs;

	@Nonnull
	private final List<Object> outputs;

	@Nonnull
	private final int cost;

	@SuppressWarnings("unchecked")
	public AlkahestryChargingRecipeJEI(@Nonnull Object input, @Nonnull ItemStack tomeInput, @Nonnull Object output, int cost) {
		this.inputs = Arrays.asList(input, tomeInput);
		this.outputs = Arrays.asList(output);
		this.cost = cost;
	}

	@Override
	public List getInputs() {
		return inputs;
	}

	@Override
	public List getOutputs() {
		return outputs;
	}
}
