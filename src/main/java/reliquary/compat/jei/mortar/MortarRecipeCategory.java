package reliquary.compat.jei.mortar;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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

public class MortarRecipeCategory implements mezz.jei.api.recipe.category.IRecipeCategory<MortarRecipeJEI> {
	public static final RecipeType<MortarRecipeJEI> TYPE = RecipeType.create(Reference.MOD_ID, "mortar", MortarRecipeJEI.class);
	private final IDrawable background;
	private final Component localizedName;
	private final IDrawable icon;

	public MortarRecipeCategory(IGuiHelper guiHelper) {

		background = guiHelper.drawableBuilder(new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png"), 137, 0, 66, 51)
				.addPadding(0, 0, 13, 0)
				.build();
		localizedName = Component.translatable("jei." + Reference.MOD_ID + ".recipe.mortar");
		icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()));
	}

	@Override
	public RecipeType<MortarRecipeJEI> getRecipeType() {
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
	public void setRecipe(IRecipeLayoutBuilder builder, MortarRecipeJEI recipe, IFocusGroup focuses) {
		List<ItemStack> inputs = recipe.getInputs();
		builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).addItemStack(inputs.get(0));
		builder.addSlot(RecipeIngredientRole.INPUT, 17, 0).addItemStack(inputs.get(1));
		IRecipeSlotBuilder thirdInput = builder.addSlot(RecipeIngredientRole.INPUT, 33, 0);
		if (inputs.size() > 2) {
			thirdInput.addItemStack(inputs.get(2));
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 34).addItemStack(recipe.getOutput());
		builder.addSlot(RecipeIngredientRole.CATALYST, 16, 34).addItemStack(new ItemStack(ModBlocks.APOTHECARY_MORTAR.get()));
	}
}
