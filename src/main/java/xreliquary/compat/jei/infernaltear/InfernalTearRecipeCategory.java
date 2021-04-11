package xreliquary.compat.jei.infernaltear;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import xreliquary.compat.jei.ReliquaryRecipeCategory;
import xreliquary.init.ModItems;
import xreliquary.items.InfernalTearItem;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;
import xreliquary.util.XpHelper;

import java.util.List;

public class InfernalTearRecipeCategory extends ReliquaryRecipeCategory<InfernalTearRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "infernal_tear");
	private static final int INPUT_SLOT_1 = 0;
	private static final ResourceLocation BACKGROUNDS_TEXTURE = new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png");

	private final IDrawable background;
	private final String localizedName;
	private final IDrawable icon;

	public InfernalTearRecipeCategory(IGuiHelper guiHelper) {
		super(UID);
		background = guiHelper.createDrawable(BACKGROUNDS_TEXTURE, 0, 76, 110, 25);
		localizedName = LanguageHelper.getLocalization("jei." + Reference.MOD_ID + ".recipe.infernal_tear");
		ItemStack iconTear = new ItemStack(ModItems.INFERNAL_TEAR.get());
		InfernalTearItem.setTearTarget(iconTear, new ItemStack(Items.IRON_INGOT));
		icon = guiHelper.createDrawableIngredient(iconTear);
	}

	@Override
	public Class<? extends InfernalTearRecipe> getRecipeClass() {
		return InfernalTearRecipe.class;
	}

	@Override
	public String getTitle() {
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
	public void setIngredients(InfernalTearRecipe recipe, IIngredients ingredients) {
		recipe.setIngredients(ingredients);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, InfernalTearRecipe recipeWrapper, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(INPUT_SLOT_1, true, 16, 0);
		List<List<ItemStack>> ingredientsInputs = ingredients.getInputs(VanillaTypes.ITEM);
		recipeLayout.getItemStacks().set(INPUT_SLOT_1, ingredientsInputs.get(0));
	}

	@Override
	public void draw(InfernalTearRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int experiencePoints = recipe.getExperiencePoints();
		String points = experiencePoints + " " + LanguageHelper.getLocalization("jei.xreliquary.recipe.infernal_tear.xp");
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		int stringWidth = fontRenderer.getStringWidth(points);
		fontRenderer.drawString(matrixStack, points, (float) ((double) background.getWidth() / 2 + (((double) background.getWidth() / 2 + 16 - stringWidth) / 2)), 5.0F, -8355712);
		drawLevels(matrixStack, experiencePoints, fontRenderer);
	}

	private void drawLevels(MatrixStack matrixStack, int experiencePoints, FontRenderer fontRenderer) {
		int numberOfLevels = XpHelper.getLevelForExperience(experiencePoints);
		drawXpBar(matrixStack, experiencePoints, numberOfLevels);
		drawXpLevel(matrixStack, fontRenderer, numberOfLevels);
	}

	private void drawXpLevel(MatrixStack matrixStack, FontRenderer fontRenderer, int numberOfLevels) {
		String xpLevel = Integer.toString(numberOfLevels);
		int x = (background.getWidth() - fontRenderer.getStringWidth(xpLevel)) / 2;
		int y = background.getHeight() - 10;

		fontRenderer.drawString(matrixStack, xpLevel, (float) (x + 1), (float) y, 0);
		fontRenderer.drawString(matrixStack, xpLevel, (float) (x - 1), (float) y, 0);
		fontRenderer.drawString(matrixStack, xpLevel, (float) x, (float) (y + 1), 0);
		fontRenderer.drawString(matrixStack, xpLevel, (float) x, (float) (y - 1), 0);
		fontRenderer.drawString(matrixStack, xpLevel, (float) x, (float) y, 8453920);
	}

	private void drawXpBar(MatrixStack matrixStack, int experiencePoints, int level) {
		int partialXp = experiencePoints - XpHelper.getExperienceForLevel(level);
		int maxBarExperience = XpHelper.getExperienceLimitOnLevel(level);

		if (partialXp == 0) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		Minecraft minecraft = Minecraft.getInstance();
		TextureManager textureManager = minecraft.getTextureManager();
		textureManager.bindTexture(BACKGROUNDS_TEXTURE);

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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		Matrix4f matrix = matrixStack.getLast().getMatrix();
		bufferBuilder.pos(matrix, x, y + height, 0.0F).tex(minU / textureWidth, maxV / textureHeight).endVertex();
		bufferBuilder.pos(matrix, x + width, y + height, 0.0F).tex(maxU / textureWidth, maxV / textureHeight).endVertex();
		bufferBuilder.pos(matrix, x + width, y, 0.0F).tex(maxU / textureWidth, minV / textureHeight).endVertex();
		bufferBuilder.pos(matrix, x, y, 0.0F).tex(minU / textureWidth, minV / textureHeight).endVertex();
		tessellator.draw();

		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
	}
}
