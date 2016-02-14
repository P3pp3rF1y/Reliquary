package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;

import javax.annotation.Nonnull;

public class AlkahestryCraftingRecipeHandler implements IRecipeHandler<AlkahestryCraftingRecipeJEI> {

    @Nonnull
    @Override
    public Class<AlkahestryCraftingRecipeJEI> getRecipeClass() {
        return AlkahestryCraftingRecipeJEI.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return Reference.JEI_CATEGORY_ALKAHESTRY_CRAFTING;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull AlkahestryCraftingRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull AlkahestryCraftingRecipeJEI recipe) {
        return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
    }
}
