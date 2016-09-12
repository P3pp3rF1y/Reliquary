package xreliquary.compat.jei.lingering;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

import javax.annotation.Nonnull;

public class ArrowShotRecipeHandler implements IRecipeHandler<ArrowShotRecipeJEI> {
	@Nonnull
	@Override
	public Class<ArrowShotRecipeJEI> getRecipeClass() {
		return ArrowShotRecipeJEI.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull ArrowShotRecipeJEI recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull ArrowShotRecipeJEI recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull ArrowShotRecipeJEI recipe) {
		return true;
	}
}
