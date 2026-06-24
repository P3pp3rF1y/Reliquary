package reliquary.compat.jei.mortar;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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

public class MortarRecipeCategory extends AbstractRecipeCategory<MortarRecipeJEI> {
	public static final IRecipeType<MortarRecipeJEI> TYPE = IRecipeType.create(Reliquary.MOD_ID, "mortar", MortarRecipeJEI.class);
	private final IDrawable background;

	public MortarRecipeCategory(IGuiHelper guiHelper) {
		super(TYPE, Component.translatable("jei." + Reliquary.MOD_ID + ".recipe.mortar"), guiHelper.createDrawableItemLike(ModBlocks.APOTHECARY_MORTAR.get()),
				78, 50);
		background = guiHelper.drawableBuilder(Reliquary.getIdentifier("textures/gui/jei/backgrounds.png"), 137, 0, 66, 51).addPadding(0, 0, 13, 2).build();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MortarRecipeJEI recipe, IFocusGroup focuses) {
		List<ItemStack> inputs = recipe.inputs();
		builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).add(inputs.get(0));
		builder.addSlot(RecipeIngredientRole.INPUT, 17, 0).add(inputs.get(1));
		IRecipeSlotBuilder thirdInput = builder.addSlot(RecipeIngredientRole.INPUT, 33, 0);
		if (inputs.size() > 2) {
			thirdInput.add(inputs.get(2));
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 34).add(recipe.output());
		builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 4, 34).add(new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()));
	}

	@Override
	public void draw(MortarRecipeJEI recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
	}
}
