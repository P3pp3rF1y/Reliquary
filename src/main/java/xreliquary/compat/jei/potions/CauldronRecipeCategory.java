package xreliquary.compat.jei.potions;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import xreliquary.compat.jei.ReliquaryPlugin;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;


public class CauldronRecipeCategory implements IRecipeCategory {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int CAULDRON_SLOT = 3;

    @Nonnull
    private final IDrawable background = ReliquaryPlugin.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/cauldron.png"), 0, 0, 107, 17);
    @Nonnull
    private final String localizedName = StatCollector.translateToLocal("jei.recipe.cauldron");

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
        recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 0);
        recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 90, 0);
        recipeLayout.getItemStacks().init(CAULDRON_SLOT, false, 45, 0);

        if (recipeWrapper instanceof CauldronRecipeJEI)
        {
            CauldronRecipeJEI mortarWrapper = (CauldronRecipeJEI) recipeWrapper;
            recipeLayout.getItemStacks().set(INPUT_SLOT, (ItemStack) mortarWrapper.getInputs().get(0));
            recipeLayout.getItemStacks().set(OUTPUT_SLOT, (ItemStack) mortarWrapper.getOutputs().get(0));
            recipeLayout.getItemStacks().set(CAULDRON_SLOT, new ItemStack(ModBlocks.apothecaryCauldron, 1));
        }

    }
}
