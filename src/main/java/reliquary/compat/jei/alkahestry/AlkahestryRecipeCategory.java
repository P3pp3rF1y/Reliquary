package reliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;
import reliquary.reference.Reference;

public abstract class AlkahestryRecipeCategory<T> implements mezz.jei.api.recipe.category.IRecipeCategory<T> {
	private final IDrawable icon;

	AlkahestryRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.drawableBuilder(new ResourceLocation(Reference.MOD_ID, "textures/item/alkahest_tome.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}
}
