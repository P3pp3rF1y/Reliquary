package reliquary.compat.jei.alkahestry;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import reliquary.crafting.AlkahestryCraftingRecipe;
import reliquary.init.ModItems;
import reliquary.items.AlkahestryTomeItem;
import reliquary.reference.Reference;
import reliquary.reference.Settings;

public class AlkahestryCraftingRecipeCategory extends AlkahestryRecipeCategory<AlkahestryCraftingRecipe> {
	public static final RecipeType<AlkahestryCraftingRecipe> TYPE = RecipeType.create(Reference.MOD_ID, "alkahestry_crafting", AlkahestryCraftingRecipe.class);
	private final IDrawable background;
	private final Component localizedName;

	public AlkahestryCraftingRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
		background = guiHelper.createDrawable(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png"), 0, 0, 95, 76);
		localizedName = Component.translatable("jei." + Reference.MOD_ID + ".recipe.alkahest_crafting");
	}

	@Override
	public RecipeType<AlkahestryCraftingRecipe> getRecipeType() {
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
	public void setRecipe(IRecipeLayoutBuilder builder, AlkahestryCraftingRecipe recipe, IFocusGroup focuses) {
		NonNullList<Ingredient> ingredientsInputs = recipe.getIngredients();
		ItemStack input = ingredientsInputs.get(0).getItems()[0];
		ItemStack tome = ingredientsInputs.get(1).getItems()[0];
		ItemStack output = recipe.getResultItem();
		ItemStack tomeOutput = AlkahestryTomeItem.setCharge(new ItemStack(ModItems.ALKAHESTRY_TOME.get()),
				Settings.COMMON.items.alkahestryTome.chargeLimit.get() - recipe.getChargeNeeded());

		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(input);
		builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addItemStack(tome);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 10).addItemStack(output);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 19, 60).addItemStack(tomeOutput);
	}

	@Override
	public void draw(AlkahestryCraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack poseStack, double mouseX, double mouseY) {
		String chargeString = "-" + recipe.getChargeNeeded();
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(chargeString);
		fontRenderer.draw(poseStack, chargeString, (float) (((double) background.getWidth() - stringWidth) / 2), 40.0F, -8355712);
	}
}
