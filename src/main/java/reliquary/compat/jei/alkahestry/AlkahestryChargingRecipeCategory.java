package reliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import reliquary.Reliquary;
import reliquary.crafting.AlkahestryChargingRecipe;

public class AlkahestryChargingRecipeCategory extends AlkahestryRecipeCategory<AlkahestryChargingRecipe> {
	public static final IRecipeType<AlkahestryChargingRecipe> TYPE = IRecipeType.create(Reliquary.MOD_ID, "alkahestry_charging",
			AlkahestryChargingRecipe.class);
	private final IDrawable background;

	public AlkahestryChargingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, TYPE, Component.translatable("jei." + Reliquary.MOD_ID + ".recipe.alkahest_charging"), 95, 36);
		background = guiHelper.createDrawable(Reliquary.getIdentifier("textures/gui/jei/backgrounds.png"), 0, 0, 95, 36);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, AlkahestryChargingRecipe recipe, IFocusGroup focuses) {
		Ingredient input = recipe.getChargingIngredient();
		Ingredient tome = recipe.getTomeIngredient();
		ItemStack output = recipe.getRecipeOutput();

		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(input);
		builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).add(tome);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 10).add(output);
	}

	@Override
	public void draw(AlkahestryChargingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
		String chargeString = "+" + recipe.getChargeToAdd();
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(chargeString);
		guiGraphics.drawString(fontRenderer, chargeString, (int) (((double) background.getWidth() - stringWidth) / 2), 3, -8355712);
	}
}
