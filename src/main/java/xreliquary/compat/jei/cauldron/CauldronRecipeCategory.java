package xreliquary.compat.jei.cauldron;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import xreliquary.compat.jei.ReliquaryPlugin;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.List;

public class CauldronRecipeCategory implements IRecipeCategory {

	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int CAULDRON_SLOT = 3;
	private static final int FIRST_ADDITIONAL_SLOT = 4;

	@Nonnull
	private final IDrawable background = ReliquaryPlugin.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/cauldron.png"), 0, 0, 107, 51);
	@Nonnull
	private final String localizedName = I18n.translateToLocal("jei.recipe.cauldron");

	@Nonnull
	@Override
	public String getUid() {
		return Reference.JEI_CATEGORY_CAULDRON;
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
		recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 33);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 90, 33);
		recipeLayout.getItemStacks().init(CAULDRON_SLOT, false, 44, 33);

		if(recipeWrapper instanceof CauldronRecipeJEI) {
			CauldronRecipeJEI cauldronWrapper = (CauldronRecipeJEI) recipeWrapper;
			initAdditionalSlots(recipeLayout, cauldronWrapper.getInputs().size());
			recipeLayout.getItemStacks().set(INPUT_SLOT, (ItemStack) cauldronWrapper.getInputs().get(0));
			recipeLayout.getItemStacks().set(OUTPUT_SLOT, (ItemStack) cauldronWrapper.getOutputs().get(0));
			recipeLayout.getItemStacks().set(CAULDRON_SLOT, new ItemStack(ModBlocks.apothecaryCauldron, 1));
			setAdditionalSlotContents(recipeLayout, cauldronWrapper.getInputs());
		}

	}

	private void setAdditionalSlotContents(IRecipeLayout recipeLayout, List inputs) {
		for(int i = 1; i < inputs.size(); i++)
			recipeLayout.getItemStacks().set((i - 1) + FIRST_ADDITIONAL_SLOT, (ItemStack) inputs.get(i));
	}

	private void initAdditionalSlots(IRecipeLayout recipeLayout, int inputCount) {
		int left = 44 - ((inputCount - 2) * 9);

		for(int i = 0; i < inputCount; i++)
			recipeLayout.getItemStacks().init(FIRST_ADDITIONAL_SLOT + i, false, (left + (i * 18)), 0);
	}
}
