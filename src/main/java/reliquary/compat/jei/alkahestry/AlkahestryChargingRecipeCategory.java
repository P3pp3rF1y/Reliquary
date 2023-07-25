package reliquary.compat.jei.alkahestry;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import reliquary.crafting.AlkahestryChargingRecipe;
import reliquary.reference.Reference;

public class AlkahestryChargingRecipeCategory extends AlkahestryRecipeCategory<AlkahestryChargingRecipe> {
	public static final RecipeType<AlkahestryChargingRecipe> TYPE = RecipeType.create(Reference.MOD_ID, "alkahestry_charging", AlkahestryChargingRecipe.class);
	private final IDrawable background;
	private final Component localizedName;

	public AlkahestryChargingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png"), 0, 0, 95, 36);
		localizedName = Component.translatable("jei." + Reference.MOD_ID + ".recipe.alkahest_charging");
	}

	@Override
	public RecipeType<AlkahestryChargingRecipe> getRecipeType() {
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
	public void setRecipe(IRecipeLayoutBuilder builder, AlkahestryChargingRecipe recipe, IFocusGroup focuses) {
		NonNullList<Ingredient> ingredientsInputs = recipe.getIngredients();
		ItemStack input = ingredientsInputs.get(0).getItems()[0];
		ItemStack tome = ingredientsInputs.get(1).getItems()[0];
		ItemStack output = recipe.getRecipeOutput();

		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
				.addItemStack(input);
		builder.addSlot(RecipeIngredientRole.INPUT, 19, 1)
				.addItemStack(tome);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 10)
				.addItemStack(output);
	}

	@Override
	public void draw(AlkahestryChargingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		String chargeString = "+" + recipe.getChargeToAdd();
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(chargeString);
		guiGraphics.drawString(fontRenderer, chargeString, (int) (((double) background.getWidth() - stringWidth) / 2), 3, -8355712);
	}
}
