package xreliquary.compat.jei.magazines;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

import javax.annotation.Nonnull;

public class MagazineRecipeHandler implements IRecipeHandler<MagazineRecipeJEI> {
	@Nonnull
	@Override
	public Class<MagazineRecipeJEI> getRecipeClass() {
		return MagazineRecipeJEI.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull MagazineRecipeJEI recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull MagazineRecipeJEI recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull MagazineRecipeJEI recipe) {
		return true;
	}
}
