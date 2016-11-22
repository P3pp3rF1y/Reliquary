package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class AlkahestryChargingRecipeHandler implements IRecipeHandler<AlkahestryChargingRecipeJEI> {

	@Nonnull
	@Override
	public Class<AlkahestryChargingRecipeJEI> getRecipeClass() {
		return AlkahestryChargingRecipeJEI.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull AlkahestryChargingRecipeJEI recipe) {
		return Reference.JEI_CATEGORY_ALKAHESTRY_CHARGING;
	}

	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull AlkahestryChargingRecipeJEI recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull AlkahestryChargingRecipeJEI recipe) {
		return recipe.getInputs().size() > 0 && recipe.getOutputs().size() > 0;
	}
}
