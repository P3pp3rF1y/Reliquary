package reliquary.compat.jei;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.ResourceLocation;

public abstract class ReliquaryRecipeCategory<T> implements IRecipeCategory<T> {
	private final ResourceLocation uid;

	protected ReliquaryRecipeCategory(ResourceLocation uid) {
		this.uid = uid;
	}

	@Override
	public ResourceLocation getUid() {
		return uid;
	}
}
