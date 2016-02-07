package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import xreliquary.compat.jei.ReliquaryPlugin;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;
import java.util.Collection;

public class AlkahestryChargingRecipeCategory implements IRecipeCategory {

    private static final int INPUT_SLOT = 0;
    private static final int TOME_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    @Nonnull
    private final IDrawable background = ReliquaryPlugin.jeiHelper.getGuiHelper().createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/alkahest_charging.png"), 0, 0, 95, 36);
    @Nonnull
    private final String localizedName = StatCollector.translateToLocal("jei.reliquary.recipe.alkahest_charging");

    @Nonnull
    @Override
    public String getUid() {
        return Reference.JEI_CATEGORY_ALKAHESTRY_CHARGING;
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

        if (recipeWrapper instanceof AlkahestryChargingRecipeJEI)
        {
            AlkahestryChargingRecipeJEI alkahestryChargingWrapper = (AlkahestryChargingRecipeJEI) recipeWrapper;
            if(alkahestryChargingWrapper.getInputs().get(0) instanceof Collection) {
                recipeLayout.getItemStacks().set(INPUT_SLOT, ((Collection<ItemStack>) alkahestryChargingWrapper.getInputs().get(0)));
            } else {
                recipeLayout.getItemStacks().set(INPUT_SLOT, (ItemStack) alkahestryChargingWrapper.getInputs().get(0));
            }
            recipeLayout.getItemStacks().set(TOME_SLOT, (ItemStack) alkahestryChargingWrapper.getInputs().get(1));

            recipeLayout.getItemStacks().set(OUTPUT_SLOT, (ItemStack) alkahestryChargingWrapper.getOutputs().get(0));
        }

    }
}
