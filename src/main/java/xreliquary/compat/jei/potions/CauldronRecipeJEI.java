package xreliquary.compat.jei.potions;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class CauldronRecipeJEI extends BlankRecipeWrapper {
    @Nonnull
    private final ItemStack input;

    @Nonnull
    private final ItemStack output;

    @SuppressWarnings("unchecked")
    public CauldronRecipeJEI(@Nonnull ItemStack input, @Nonnull ItemStack output)
    {
        this.input = input;
        this.output = output;
    }

    @Override
    public List getInputs()
    {
        return Collections.singletonList(input);
    }

    @Override
    public List getOutputs(){ return Collections.singletonList(output);
    }
}
