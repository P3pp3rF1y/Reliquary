package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class AlkahestryCraftingRecipeCategory extends BlankRecipeCategory<AlkahestryCraftingRecipeJEI> {

	private static final int INPUT_SLOT = 0;
	private static final int TOME_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;
	private static final int TOME_OUTPUT_SLOT = 3;

	private final IDrawable background;
	private final String localizedName;

	public AlkahestryCraftingRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/alkahest_crafting.png"), 0, 0, 95, 76);
		localizedName = LanguageHelper.getLocalization("jei.recipe.alkahest_crafting");
	}

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
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AlkahestryCraftingRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 0);
		recipeLayout.getItemStacks().init(TOME_SLOT, true, 18, 0);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 73, 9);
		recipeLayout.getItemStacks().init(TOME_OUTPUT_SLOT, false, 18, 59);

		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(ItemStack.class);
		ItemStack input = ingredientsInputs.get(0).get(0);
		ItemStack tome = ingredientsInputs.get(1).get(0);
		ItemStack output = ingredients.getOutputs(ItemStack.class).get(0).get(0);
		ItemStack tomeOutput = ingredients.getOutputs(ItemStack.class).get(0).get(1);

		recipeLayout.getItemStacks().set(INPUT_SLOT, input);
		recipeLayout.getItemStacks().set(TOME_SLOT, tome);
		recipeLayout.getItemStacks().set(OUTPUT_SLOT, output);
		recipeLayout.getItemStacks().set(TOME_OUTPUT_SLOT, tomeOutput);
	}
}
