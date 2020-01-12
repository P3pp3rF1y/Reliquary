package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.reference.Reference;

public abstract class AlkahestryRecipeCategory<T> extends ReliquaryRecipeCategory<T> {
	private final IDrawable icon;

	AlkahestryRecipeCategory(IGuiHelper guiHelper, ResourceLocation uid) {
		super(uid);
		icon = guiHelper.drawableBuilder(new ResourceLocation(Reference.DOMAIN + "textures/items/alkahest_tome.png"), 0, 0, 16, 16)
				.setTextureSize(16, 16).build();
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}
}
