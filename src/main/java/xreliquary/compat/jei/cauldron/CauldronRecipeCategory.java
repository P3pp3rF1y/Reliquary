package xreliquary.compat.jei.cauldron;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.compat.jei.JEICategory;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class CauldronRecipeCategory extends ReliquaryRecipeCategory<CauldronRecipeJEI> {
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int CAULDRON_SLOT = 3;
	private static final int FIRST_ADDITIONAL_SLOT = 4;

	private final IDrawable background;
	private final String localizedName;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/cauldron.png"), 0, 0, 107, 51);
		localizedName = LanguageHelper.getLocalization("jei.recipe.cauldron");
	}

	@Nonnull
	@Override
	public String getUid() {
		return JEICategory.CAULDRON.getUid();
	}

	@Nonnull
	@Override
	public String getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public String getModName() {
		return Reference.MOD_NAME;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CauldronRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 33);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 90, 33);
		recipeLayout.getItemStacks().init(CAULDRON_SLOT, false, 44, 33);

		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(ItemStack.class);
		ItemStack output = ingredients.getOutputs(ItemStack.class).get(0).get(0);

		initAdditionalSlots(recipeLayout, ingredientsInputs.size());
		recipeLayout.getItemStacks().set(INPUT_SLOT, ingredientsInputs.get(0));
		recipeLayout.getItemStacks().set(OUTPUT_SLOT, output);
		recipeLayout.getItemStacks().set(CAULDRON_SLOT, new ItemStack(ModBlocks.apothecaryCauldron, 1));
		setAdditionalSlotContents(recipeLayout, ingredientsInputs);
	}

	private void setAdditionalSlotContents(IRecipeLayout recipeLayout, List<List<ItemStack>> inputs) {
		for(int i = 1; i < inputs.size(); i++)
			recipeLayout.getItemStacks().set((i - 1) + FIRST_ADDITIONAL_SLOT, inputs.get(i));
	}

	private void initAdditionalSlots(IRecipeLayout recipeLayout, int inputCount) {
		int left = 44 - ((inputCount - 2) * 9);

		for(int i = 0; i < inputCount; i++)
			recipeLayout.getItemStacks().init(FIRST_ADDITIONAL_SLOT + i, false, (left + (i * 18)), 0);
	}
}
