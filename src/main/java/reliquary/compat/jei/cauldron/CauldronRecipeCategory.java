package reliquary.compat.jei.cauldron;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModBlocks;
import reliquary.reference.Reference;

import java.util.List;

public class CauldronRecipeCategory implements mezz.jei.api.recipe.category.IRecipeCategory<CauldronRecipeJEI> {
	public static final RecipeType<CauldronRecipeJEI> TYPE = RecipeType.create(Reference.MOD_ID, "cauldron", CauldronRecipeJEI.class);
	private final IDrawable background;
	private final Component localizedName;
	private final IDrawable icon;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png"), 96, 0, 107, 51);
		localizedName = Component.translatable("jei." + Reference.MOD_ID + ".recipe.cauldron");
		icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()));
	}

	@Override
	public RecipeType<CauldronRecipeJEI> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
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
	public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipeJEI recipe, IFocusGroup focuses) {
		List<ItemStack> ingredientsInputs = recipe.getInputs();

		builder.addSlot(RecipeIngredientRole.INPUT, 0, 33).addItemStack(ingredientsInputs.get(0));
		setAdditionalSlotContents(builder, ingredientsInputs);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 33).addItemStack(recipe.getOutput());
		builder.addSlot(RecipeIngredientRole.CATALYST, 44, 33).addItemStack(new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()));
	}

	private void setAdditionalSlotContents(IRecipeLayoutBuilder builder, List<ItemStack> inputs) {
		int left = 44 - ((inputs.size() - 2) * 9);
		for (int i = 1; i < inputs.size(); i++) {
			builder.addSlot(RecipeIngredientRole.INPUT, (left + (i * 18)), 0).addItemStack(inputs.get(i));
		}
	}
}
