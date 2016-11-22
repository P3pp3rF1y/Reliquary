package xreliquary.compat.jei.mortar;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class MortarRecipeCategory extends BlankRecipeCategory<MortarRecipeJEI> {

	private static final int INPUT_SLOT_1 = 0;
	private static final int INPUT_SLOT_2 = 1;
	private static final int INPUT_SLOT_3 = 2;
	private static final int OUTPUT_SLOT = 3;
	private static final int MORTAR_SLOT = 4;

	private final IDrawable background;
	private final String localizedName;

	public MortarRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/mortar.png"), 0, 0, 79, 51);
		localizedName = LanguageHelper.getLocalization("jei.recipe.mortar");
	}

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
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull MortarRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT_1, true, 0, 0);
		recipeLayout.getItemStacks().init(INPUT_SLOT_2, true, 17, 0);
		recipeLayout.getItemStacks().init(INPUT_SLOT_3, true, 33, 0);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 62, 34);
		recipeLayout.getItemStacks().init(MORTAR_SLOT, false, 16, 34);

		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(ItemStack.class);
		ItemStack output = ingredients.getOutputs(ItemStack.class).get(0).get(0);

		recipeLayout.getItemStacks().set(INPUT_SLOT_1, ingredientsInputs.get(0));
		recipeLayout.getItemStacks().set(INPUT_SLOT_2, ingredientsInputs.get(1));
		if(ingredientsInputs.size() == 3)
			recipeLayout.getItemStacks().set(INPUT_SLOT_3, ingredientsInputs.get(2));
		recipeLayout.getItemStacks().set(OUTPUT_SLOT, output);
		recipeLayout.getItemStacks().set(MORTAR_SLOT, new ItemStack(ModBlocks.apothecaryMortar, 1));
	}
}
