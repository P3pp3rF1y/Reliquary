package xreliquary.compat.jei;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public abstract class ReliquaryRecipeCategory<T> implements IRecipeCategory<T> {
	private final ResourceLocation uid;

	public ReliquaryRecipeCategory(ResourceLocation uid) {
		this.uid = uid;
	}

	@Override
	public ResourceLocation getUid() {
		return uid;
	}
}
