package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import xreliquary.compat.jei.ReliquaryPlugin;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.Collection;

public class AlkahestryCraftingRecipeCategory implements IRecipeCategory {

	private static final int INPUT_SLOT = 0;
	private static final int TOME_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;
	private static final int TOME_OUTPUT_SLOT = 3;

	@Nonnull
	private final IDrawable background = ReliquaryPlugin.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/alkahest_crafting.png"), 0, 0, 95, 76);
	@Nonnull
	private final String localizedName = I18n.translateToLocal("jei.recipe.alkahest_crafting");

	@Nonnull
	@Override
	public String getUid() {
		return Reference.JEI_CATEGORY_ALKAHESTRY_CRAFTING;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {

	}

	@Override
	public void drawAnimations(Minecraft minecraft) {

	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
		recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 0);
		recipeLayout.getItemStacks().init(TOME_SLOT, true, 18, 0);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 73, 9);
		recipeLayout.getItemStacks().init(TOME_OUTPUT_SLOT, false, 18, 59);

		if(recipeWrapper instanceof AlkahestryCraftingRecipeJEI) {
			AlkahestryCraftingRecipeJEI alkahestryCraftingWrapper = (AlkahestryCraftingRecipeJEI) recipeWrapper;
			if(alkahestryCraftingWrapper.getInputs().get(0) instanceof Collection) {
				recipeLayout.getItemStacks().set(INPUT_SLOT, ((Collection<ItemStack>) alkahestryCraftingWrapper.getInputs().get(0)));
			} else {
				recipeLayout.getItemStacks().set(INPUT_SLOT, (ItemStack) alkahestryCraftingWrapper.getInputs().get(0));
			}
			recipeLayout.getItemStacks().set(TOME_SLOT, (ItemStack) alkahestryCraftingWrapper.getInputs().get(1));

			if(alkahestryCraftingWrapper.getOutputs().get(0) instanceof Collection) {
				recipeLayout.getItemStacks().set(OUTPUT_SLOT, ((Collection<ItemStack>) alkahestryCraftingWrapper.getOutputs().get(0)));
			} else {
				recipeLayout.getItemStacks().set(OUTPUT_SLOT, (ItemStack) alkahestryCraftingWrapper.getOutputs().get(0));
			}
			recipeLayout.getItemStacks().set(TOME_OUTPUT_SLOT, (ItemStack) alkahestryCraftingWrapper.getOutputs().get(1));
		}

	}
}
