package xreliquary.compat.jei.mortar;

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

public class MortarRecipeCategory extends ReliquaryRecipeCategory<MortarRecipeJEI> {
	public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "mortar");
	private static final int INPUT_SLOT_1 = 0;
	private static final int INPUT_SLOT_2 = 1;
	private static final int INPUT_SLOT_3 = 2;
	private static final int OUTPUT_SLOT = 3;
	private static final int MORTAR_SLOT = 4;

	private final IDrawable background;
	private final String localizedName;
	private final IDrawable icon;

	public MortarRecipeCategory(IGuiHelper guiHelper) {
		super(UID);
		background = guiHelper.drawableBuilder(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png"), 137, 0, 66, 51)
				.addPadding(0, 0, 13, 0)
				.build();
		localizedName = LanguageHelper.getLocalization("jei." + Reference.MOD_ID + ".recipe.mortar");
		icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()));
	}

	@Override
	public Class<? extends MortarRecipeJEI> getRecipeClass() {
		return MortarRecipeJEI.class;
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
	public void setIngredients(MortarRecipeJEI recipe, IIngredients ingredients) {
		recipe.setIngredients(ingredients);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MortarRecipeJEI recipeWrapper, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT_1, true, 0, 0);
		recipeLayout.getItemStacks().init(INPUT_SLOT_2, true, 17, 0);
		recipeLayout.getItemStacks().init(INPUT_SLOT_3, true, 33, 0);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 62, 34);
		recipeLayout.getItemStacks().init(MORTAR_SLOT, false, 16, 34);

		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(VanillaTypes.ITEM);
		ItemStack output = ingredients.getOutputs(VanillaTypes.ITEM).get(0).get(0);

		recipeLayout.getItemStacks().set(INPUT_SLOT_1, ingredientsInputs.get(0));
		recipeLayout.getItemStacks().set(INPUT_SLOT_2, ingredientsInputs.get(1));
		if (ingredientsInputs.size() == 3) {
			recipeLayout.getItemStacks().set(INPUT_SLOT_3, ingredientsInputs.get(2));
		}
		recipeLayout.getItemStacks().set(OUTPUT_SLOT, output);
		recipeLayout.getItemStacks().set(MORTAR_SLOT, new ItemStack(ModBlocks.APOTHECARY_MORTAR.get(), 1));
	}
}
