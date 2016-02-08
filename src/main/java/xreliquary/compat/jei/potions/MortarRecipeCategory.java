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
import xreliquary.compat.jei.alkahestry.AlkahestryChargingRecipeJEI;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.Collection;


public class MortarRecipeCategory implements IRecipeCategory {

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 0;
    private static final int INPUT_SLOT_3 = 0;
    private static final int OUTPUT_SLOT = 1;

    @Nonnull
    private final IDrawable background = ReliquaryPlugin.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/mortar.png"), 0, 0, 95, 36);
    @Nonnull
    private final String localizedName = StatCollector.translateToLocal("jei.recipe.mortar");

    @Nonnull
    @Override
    public String getUid() {
        return Reference.JEI_CATEGORY_MORTAR;
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
        recipeLayout.getItemStacks().init(INPUT_SLOT_1, true, 0, 0);
        recipeLayout.getItemStacks().init(INPUT_SLOT_2, true, 20, 0);
        recipeLayout.getItemStacks().init(INPUT_SLOT_3, true, 40, 0);
        recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 73, 9);

        if (recipeWrapper instanceof MortarRecipeJEI)
        {
            MortarRecipeJEI mortarWrapper = (MortarRecipeJEI) recipeWrapper;
            recipeLayout.getItemStacks().set(INPUT_SLOT_1, (ItemStack) mortarWrapper.getInputs().get(0));
            recipeLayout.getItemStacks().set(INPUT_SLOT_2, (ItemStack) mortarWrapper.getInputs().get(1));
            recipeLayout.getItemStacks().set(INPUT_SLOT_3, (ItemStack) mortarWrapper.getInputs().get(2));
            recipeLayout.getItemStacks().set(OUTPUT_SLOT, (ItemStack) mortarWrapper.getOutputs().get(0));
        }

    }
}
