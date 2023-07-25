package reliquary.compat.jei.infernaltear;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;
import reliquary.init.ModItems;
import reliquary.items.InfernalTearItem;
import reliquary.reference.Reference;
import reliquary.util.XpHelper;

public class InfernalTearRecipeCategory implements mezz.jei.api.recipe.category.IRecipeCategory<InfernalTearRecipe> {
	public static final RecipeType<InfernalTearRecipe> TYPE = RecipeType.create(Reference.MOD_ID, "infernal_tear", InfernalTearRecipe.class);
	private static final ResourceLocation BACKGROUNDS_TEXTURE = new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png");

	private final IDrawable background;
	private final Component localizedName;
	private final IDrawable icon;

	public InfernalTearRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUNDS_TEXTURE, 0, 76, 110, 25);
		localizedName = Component.translatable("jei." + Reference.MOD_ID + ".recipe.infernal_tear");
		ItemStack iconTear = new ItemStack(ModItems.INFERNAL_TEAR.get());
		InfernalTearItem.setTearTarget(iconTear, new ItemStack(Items.IRON_INGOT));
		icon = guiHelper.createDrawableItemStack(iconTear);
	}

	@Override
	public RecipeType<InfernalTearRecipe> getRecipeType() {
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
	public void setRecipe(IRecipeLayoutBuilder builder, InfernalTearRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 0).addItemStack(recipe.getInput());
	}

	@Override
	public void draw(InfernalTearRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		int experiencePoints = recipe.getExperiencePoints();
		String points = experiencePoints + " " + Language.getInstance().getOrDefault("jei.reliquary.recipe.infernal_tear.xp");
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(points);
		guiGraphics.drawString(fontRenderer, points, (int) ((double) background.getWidth() / 2 + (((double) background.getWidth() / 2 + 16 - stringWidth) / 2)), 5, -8355712);
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

		guiGraphics.drawString(fontRenderer, xpLevel, x + 1, y, 0);
		guiGraphics.drawString(fontRenderer, xpLevel, x - 1, y, 0);
		guiGraphics.drawString(fontRenderer, xpLevel, x, y + 1, 0);
		guiGraphics.drawString(fontRenderer, xpLevel, x, y - 1, 0);
		guiGraphics.drawString(fontRenderer, xpLevel, x, y, 8453920);
	}

	private void drawXpBar(GuiGraphics guiGraphics, int experiencePoints, int level) {
		int partialXp = experiencePoints - XpHelper.getExperienceForLevel(level);
		int maxBarExperience = XpHelper.getExperienceLimitOnLevel(level);

		if (partialXp == 0) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, BACKGROUNDS_TEXTURE);

		float textureWidth = 256;
		float textureHeight = 256;

		float minU = 0;
		float minV = 101;
		float maxU = 110 * ((float) partialXp / maxBarExperience);
		float maxV = 106;
		float width = maxU - minU;
		float height = maxV - minV;
		float x = 0;
		float y = (float) background.getHeight() - 5;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.getBuilder();
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		Matrix4f matrix = guiGraphics.pose().last().pose();
		bufferBuilder.vertex(matrix, x, y + height, 0.0F).uv(minU / textureWidth, maxV / textureHeight).endVertex();
		bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).uv(maxU / textureWidth, maxV / textureHeight).endVertex();
		bufferBuilder.vertex(matrix, x + width, y, 0.0F).uv(maxU / textureWidth, minV / textureHeight).endVertex();
		bufferBuilder.vertex(matrix, x, y, 0.0F).uv(minU / textureWidth, minV / textureHeight).endVertex();
		tesselator.end();

		RenderSystem.disableBlend();
	}
}
