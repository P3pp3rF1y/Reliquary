package xreliquary.compat.jei.cauldron;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;


public class CauldronSplashRecipeHandler implements IRecipeHandler<CauldronSplashRecipeJEI> {

    @Nonnull
    @Override
    public Class<CauldronSplashRecipeJEI> getRecipeClass() {
        return CauldronSplashRecipeJEI.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return Reference.JEI_CATEGORY_CAULDRON_SPLASH;
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull CauldronSplashRecipeJEI recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull CauldronSplashRecipeJEI recipe) {
        return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
    }
}
