package reliquary.compat.jei.infernaltear;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import reliquary.Reliquary;
import reliquary.init.ModItems;
import reliquary.item.InfernalTearItem;
import reliquary.util.XpHelper;

public class InfernalTearRecipeCategory extends AbstractRecipeCategory<InfernalTearRecipe> {
	public static final IRecipeType<InfernalTearRecipe> TYPE = IRecipeType.create(Reliquary.MOD_ID, "infernal_tear", InfernalTearRecipe.class);
	private static final ResourceLocation BACKGROUNDS_TEXTURE = Reliquary.getRL("textures/gui/jei/backgrounds.png");
	public static final int BLACK_COLOR = ARGB.opaque(0);
	public static final int XP_COLOR = ARGB.opaque(8453920);

	private final IDrawable background;

	private static IDrawable getIcon(IGuiHelper guiHelper) {
		ItemStack iconTear = new ItemStack(ModItems.INFERNAL_TEAR.get());
		InfernalTearItem.setTearTarget(iconTear, new ItemStack(Items.IRON_INGOT));
		return guiHelper.createDrawableItemStack(iconTear);
	}

	public InfernalTearRecipeCategory(IGuiHelper guiHelper) {
		super(TYPE, Component.translatable("jei." + Reliquary.MOD_ID + ".recipe.infernal_tear"), getIcon(guiHelper), 110, 25);
		background = guiHelper.createDrawable(BACKGROUNDS_TEXTURE, 0, 76, 110, 25);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, InfernalTearRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 0).add(recipe.getInput());
	}

	@Override
	public void draw(InfernalTearRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);

		int experiencePoints = recipe.getExperiencePoints();
		String points = experiencePoints + " " + Language.getInstance().getOrDefault("jei.reliquary.recipe.infernal_tear.xp");
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(points);
		guiGraphics.drawString(fontRenderer, points, (int) ((double) background.getWidth() / 2 + (((double) background.getWidth() / 2 + 16 - stringWidth) / 2)), 5, XP_COLOR);
		drawLevels(guiGraphics, experiencePoints, fontRenderer);
	}

	private void drawLevels(GuiGraphics guiGraphics, int experiencePoints, Font fontRenderer) {
		int numberOfLevels = XpHelper.getLevelForExperience(experiencePoints);
		drawXpBar(guiGraphics, experiencePoints, numberOfLevels);
		drawXpLevel(guiGraphics, fontRenderer, numberOfLevels);
	}

	private void drawXpLevel(GuiGraphics guiGraphics, Font fontRenderer, int numberOfLevels) {
		String xpLevel = Integer.toString(numberOfLevels);
		int x = (background.getWidth() - fontRenderer.width(xpLevel)) / 2;
		int y = background.getHeight() - 10;

		guiGraphics.drawString(fontRenderer, xpLevel, x + 1, y, BLACK_COLOR);
		guiGraphics.drawString(fontRenderer, xpLevel, x - 1, y, BLACK_COLOR);
		guiGraphics.drawString(fontRenderer, xpLevel, x, y + 1, BLACK_COLOR);
		guiGraphics.drawString(fontRenderer, xpLevel, x, y - 1, BLACK_COLOR);
		guiGraphics.drawString(fontRenderer, xpLevel, x, y, XP_COLOR);
	}

	private void drawXpBar(GuiGraphics guiGraphics, int experiencePoints, int level) {
		int partialXp = experiencePoints - XpHelper.getExperienceForLevel(level);
		int maxBarExperience = XpHelper.getExperienceLimitOnLevel(level);

		if (partialXp == 0) {
			return;
		}

		float minU = 0;
		float minV = 101;
		float maxU = 110 * ((float) partialXp / maxBarExperience);
		float maxV = 106;
		int width = (int) (maxU - minU);
		int height = (int) (maxV - minV);
		int x = 0;
		int y = background.getHeight() - 5;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUNDS_TEXTURE, x, y, minU, minV, width, height, 256, 256);
	}
}
