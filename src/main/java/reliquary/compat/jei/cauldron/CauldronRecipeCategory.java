package reliquary.compat.jei.cauldron;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;
import reliquary.init.ModBlocks;

import java.util.List;

public class CauldronRecipeCategory extends AbstractRecipeCategory<CauldronRecipeJEI> {
	public static final IRecipeType<CauldronRecipeJEI> TYPE = IRecipeType.create(Reliquary.MOD_ID, "cauldron", CauldronRecipeJEI.class);
	private final IDrawable background;

	public CauldronRecipeCategory(IGuiHelper guiHelper) {
		super(TYPE, Component.translatable("jei." + Reliquary.MOD_ID + ".recipe.cauldron"), guiHelper.createDrawableItemLike(ModBlocks.APOTHECARY_CAULDRON.get()), 107, 51);
		background = guiHelper.createDrawable(Reliquary.getIdentifier("textures/gui/jei/backgrounds.png"), 96, 0, 107, 51);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipeJEI recipe, IFocusGroup focuses) {
		List<ItemStack> ingredientsInputs = recipe.getInputs();

		builder.addSlot(RecipeIngredientRole.INPUT, 0, 33).add(ingredientsInputs.getFirst());
		setAdditionalSlotContents(builder, ingredientsInputs);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 33).add(recipe.getOutput());
		builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 44, 33).add(new ItemStack(ModBlocks.APOTHECARY_CAULDRON.get()));
	}

	private void setAdditionalSlotContents(IRecipeLayoutBuilder builder, List<ItemStack> inputs) {
		int left = 44 - ((inputs.size() - 2) * 9);
		for (int i = 1; i < inputs.size(); i++) {
			builder.addSlot(RecipeIngredientRole.INPUT, (left + (i * 18)), 0).add(inputs.get(i));
		}
	}

	@Override
	public void draw(CauldronRecipeJEI recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
	}
}
