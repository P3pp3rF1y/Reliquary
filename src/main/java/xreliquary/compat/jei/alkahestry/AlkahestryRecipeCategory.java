package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.init.ModItems;

public abstract class AlkahestryRecipeCategory<T> extends ReliquaryRecipeCategory<T> {
	private final IDrawable icon;

	AlkahestryRecipeCategory(IGuiHelper guiHelper, ResourceLocation uid) {
		super(uid);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.ALKAHESTRY_TOME));
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}
}
