package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.ResourceLocation;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;

public abstract class AlkahestryRecipeCategory<T extends IRecipeWrapper> extends ReliquaryRecipeCategory<T> {

	private final IDrawable icon;

	AlkahestryRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/items/alkahest_tome.png"), 0, 0, 16, 16, 16, 16);
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}
}
