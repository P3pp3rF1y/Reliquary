package xreliquary.compat.jei.cauldron;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.init.ModBlocks;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import java.util.List;

public class CauldronRecipeCategory extends ReliquaryRecipeCategory<CauldronRecipeJEI> {
	public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "cauldron");
	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int CAULDRON_SLOT = 3;
	private static final int FIRST_ADDITIONAL_SLOT = 4;

	private final IDrawable background;
	private final String localizedName;
	private final IDrawable icon;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		super(UID);
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/cauldron.png"), 0, 0, 107, 51);
		localizedName = LanguageHelper.getLocalization("jei." + Reference.MOD_ID + ".recipe.cauldron");
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()));
	}

	@Override
	public Class<? extends CauldronRecipeJEI> getRecipeClass() {
		return CauldronRecipeJEI.class;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(CauldronRecipeJEI recipe, IIngredients ingredients) {
		recipe.setIngredients(ingredients);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CauldronRecipeJEI recipeWrapper, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 33);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 90, 33);
		recipeLayout.getItemStacks().init(CAULDRON_SLOT, false, 44, 33);

		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(VanillaTypes.ITEM);
		ItemStack output = ingredients.getOutputs(VanillaTypes.ITEM).get(0).get(0);

		initAdditionalSlots(recipeLayout, ingredientsInputs.size());
		recipeLayout.getItemStacks().set(INPUT_SLOT, ingredientsInputs.get(0));
		recipeLayout.getItemStacks().set(OUTPUT_SLOT, output);
		recipeLayout.getItemStacks().set(CAULDRON_SLOT, new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get(), 1));
		setAdditionalSlotContents(recipeLayout, ingredientsInputs);
	}

	private void setAdditionalSlotContents(IRecipeLayout recipeLayout, List<List<ItemStack>> inputs) {
		for (int i = 1; i < inputs.size(); i++) {
			recipeLayout.getItemStacks().set((i - 1) + FIRST_ADDITIONAL_SLOT, inputs.get(i));
		}
	}

	private void initAdditionalSlots(IRecipeLayout recipeLayout, int inputCount) {
		int left = 44 - ((inputCount - 2) * 9);

		for (int i = 0; i < inputCount; i++) {
			recipeLayout.getItemStacks().init(FIRST_ADDITIONAL_SLOT + i, false, (left + (i * 18)), 0);
		}
	}
}
