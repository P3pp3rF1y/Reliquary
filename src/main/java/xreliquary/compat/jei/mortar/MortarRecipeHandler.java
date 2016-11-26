package xreliquary.compat.jei.mortar;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class MortarRecipeHandler implements IRecipeHandler<MortarRecipeJEI> {

	@Nonnull
	@Override
	public Class<MortarRecipeJEI> getRecipeClass() {
		return MortarRecipeJEI.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull MortarRecipeJEI recipe) {
		return Reference.JEI_CATEGORY_MORTAR;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull MortarRecipeJEI recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull MortarRecipeJEI recipe) {
		return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
	}
}
