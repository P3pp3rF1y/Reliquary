package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.init.ModItems;

public abstract class AlkahestryRecipeCategory<T> extends ReliquaryRecipeCategory<T> {
	private final IDrawable icon;

	AlkahestryRecipeCategory(IGuiHelper guiHelper, ResourceLocation uid) {
		super(uid);
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.ALKAHESTRY_TOME.get()));
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}
}
