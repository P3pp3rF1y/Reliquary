package xreliquary.compat.jei;

import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public abstract class ReliquaryRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

	@Nonnull
	@Override
	public String getModName() {
		return Reference.MOD_NAME;
	}
}
