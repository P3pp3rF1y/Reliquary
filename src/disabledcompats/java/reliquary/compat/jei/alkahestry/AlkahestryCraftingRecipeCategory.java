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
import net.neoforged.neoforge.common.crafting.CustomDisplayIngredient;
import reliquary.Reliquary;
import reliquary.crafting.AlkahestryCraftingRecipe;
import reliquary.init.ModItems;
import reliquary.item.AlkahestryTomeItem;

public class AlkahestryCraftingRecipeCategory extends AlkahestryRecipeCategory<AlkahestryCraftingRecipe> {
	public static final IRecipeType<AlkahestryCraftingRecipe> TYPE = IRecipeType.create(Reliquary.MOD_ID, "alkahestry_crafting", AlkahestryCraftingRecipe.class);
	private final IDrawable background;

	public AlkahestryCraftingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper, TYPE, Component.translatable("jei." + Reliquary.MOD_ID + ".recipe.alkahest_crafting"), 95, 76);
		background = guiHelper.createDrawable(Reliquary.getIdentifier("textures/gui/jei/backgrounds.png"), 0, 0, 95, 76);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, AlkahestryCraftingRecipe recipe, IFocusGroup focuses) {
		Ingredient input = recipe.getCraftingIngredient();
		Ingredient tome = new AlkahestryCraftingRecipe.TomeIngredient(AlkahestryTomeItem.getChargeLimit()).toVanilla();
		ItemStack output = recipe.getResultItem();
		ItemStack tomeOutput = AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()),
				AlkahestryTomeItem.getChargeLimit() - recipe.getChargeNeeded());

		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(input);
		builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).add(tome);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 10).add(output);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 19, 60).add(tomeOutput);
	}

	@Override
	public void draw(AlkahestryCraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
		String chargeString = "-" + recipe.getChargeNeeded();
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(chargeString);
		guiGraphics.drawString(fontRenderer, chargeString, (int) (((double) background.getWidth() - stringWidth) / 2), 40, -8355712);
	}
}
