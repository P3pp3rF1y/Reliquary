package xreliquary.compat.jei.cauldron;

import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;


public class CauldronSplashRecipeJEI extends BlankRecipeWrapper {
    @Nonnull
    private final ItemStack input;

    @Nonnull
    private final ItemStack output;

    @SuppressWarnings("unchecked")
    public CauldronSplashRecipeJEI(@Nonnull ItemStack input, @Nonnull ItemStack output)
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
