package xreliquary.compat.jei.cauldron;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class CauldronRecipeHandler implements IRecipeHandler<CauldronRecipeJEI> {

	@Nonnull
	@Override
	public Class<CauldronRecipeJEI> getRecipeClass() {
		return CauldronRecipeJEI.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return Reference.JEI_CATEGORY_CAULDRON;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull CauldronRecipeJEI recipe) {
		return Reference.JEI_CATEGORY_CAULDRON;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull CauldronRecipeJEI recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull CauldronRecipeJEI recipe) {
		return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
	}
}
