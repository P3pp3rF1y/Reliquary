package xreliquary.compat.jei.alkahestry;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.compat.jei.JEICategory;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import javax.annotation.Nonnull;
import java.util.List;

public class AlkahestryChargingRecipeCategory extends AlkahestryRecipeCategory<AlkahestryChargingRecipeJEI> {

	private static final int INPUT_SLOT = 0;
	private static final int TOME_SLOT = 1;
	private static final int OUTPUT_SLOT = 2;

	private final IDrawable background;
	private final String localizedName;

	public AlkahestryChargingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/alkahest_charging.png"), 0, 0, 95, 36);
		localizedName = LanguageHelper.getLocalization("jei.recipe.alkahest_charging");
	}

	@Nonnull
	@Override
	public String getUid() {
		return JEICategory.ALKAHESTRY_CHARGING.getUid();
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
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AlkahestryChargingRecipeJEI recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 0);
		recipeLayout.getItemStacks().init(TOME_SLOT, true, 18, 0);
		recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 73, 9);

		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(ItemStack.class);
		ItemStack input = ingredientsInputs.get(0).get(0);
		ItemStack tome = ingredientsInputs.get(1).get(0);
		ItemStack output = ingredients.getOutputs(ItemStack.class).get(0).get(0);

		recipeLayout.getItemStacks().set(INPUT_SLOT, input);
		recipeLayout.getItemStacks().set(TOME_SLOT, tome);
		recipeLayout.getItemStacks().set(OUTPUT_SLOT, output);
	}
}
