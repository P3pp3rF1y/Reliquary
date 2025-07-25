package reliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import reliquary.Reliquary;

public abstract class AlkahestryRecipeCategory<T> extends AbstractRecipeCategory<T> {
	AlkahestryRecipeCategory(IGuiHelper guiHelper, IRecipeType<T> recipeType, Component title, int width, int height) {
		super(recipeType, title, guiHelper.drawableBuilder(Reliquary.getRL("textures/item/alkahestry_tome.png"), 0, 0, 16, 16).setTextureSize(16, 16).build(), width, height);
	}
}
