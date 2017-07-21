package xreliquary.compat.jei;

import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public abstract class ReliquaryRecipeCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> {

	@Nonnull
	@Override
	public String getModName() {
		return Reference.MOD_NAME;
	}
}
