package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlkahestryCraftingRecipeJEI extends BlankRecipeWrapper {
    @Nonnull
    private final List<Object> inputs;

    @Nonnull
    private final List<Object> outputs;

    @Nonnull
    private final int cost;

    @SuppressWarnings("unchecked")
    public AlkahestryCraftingRecipeJEI(@Nonnull Object input, @Nonnull ItemStack tomeInput, @Nonnull Object output, @Nonnull ItemStack tomeOutput, int cost)
    {
        this.inputs = Arrays.asList(input, tomeInput);
        this.outputs = Arrays.asList(output, tomeOutput);
        this.cost = cost;
    }

    @Override
    public List getInputs()
    {
        return inputs;
    }

    @Override
    public List getOutputs()
    {
        return outputs;
    }
}
