package reliquary.compat.jei.infernaltear;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import reliquary.compat.jei.ReliquaryRecipeCategory;
import reliquary.init.ModItems;
import reliquary.items.InfernalTearItem;
import reliquary.reference.Reference;
import reliquary.util.LanguageHelper;
import reliquary.util.XpHelper;

import java.util.List;

public class InfernalTearRecipeCategory extends ReliquaryRecipeCategory<InfernalTearRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "infernal_tear");
	private static final int INPUT_SLOT_1 = 0;
	private static final ResourceLocation BACKGROUNDS_TEXTURE = new ResourceLocation(Reference.DOMAIN + "textures/gui/jei/backgrounds.png");

	private final IDrawable background;
	private final Component localizedName;
	private final IDrawable icon;

	public InfernalTearRecipeCategory(IGuiHelper guiHelper) {
		super(UID);
		background = guiHelper.createDrawable(BACKGROUNDS_TEXTURE, 0, 76, 110, 25);
		localizedName = new TranslatableComponent("jei." + Reference.MOD_ID + ".recipe.infernal_tear");
		ItemStack iconTear = new ItemStack(ModItems.INFERNAL_TEAR.get());
		InfernalTearItem.setTearTarget(iconTear, new ItemStack(Items.IRON_INGOT));
		icon = guiHelper.createDrawableIngredient(iconTear);
	}

	@Override
	public Class<? extends InfernalTearRecipe> getRecipeClass() {
		return InfernalTearRecipe.class;
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
	public void draw(InfernalTearRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		int experiencePoints = recipe.getExperiencePoints();
		String points = experiencePoints + " " + LanguageHelper.getLocalization("jei.reliquary.recipe.infernal_tear.xp");
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(points);
		fontRenderer.draw(matrixStack, points, (float) ((double) background.getWidth() / 2 + (((double) background.getWidth() / 2 + 16 - stringWidth) / 2)), 5.0F, -8355712);
		drawLevels(matrixStack, experiencePoints, fontRenderer);
	}

	private void drawLevels(PoseStack matrixStack, int experiencePoints, Font fontRenderer) {
		int numberOfLevels = XpHelper.getLevelForExperience(experiencePoints);
		drawXpBar(matrixStack, experiencePoints, numberOfLevels);
		drawXpLevel(matrixStack, fontRenderer, numberOfLevels);
	}

	private void drawXpLevel(PoseStack matrixStack, Font fontRenderer, int numberOfLevels) {
		String xpLevel = Integer.toString(numberOfLevels);
		int x = (background.getWidth() - fontRenderer.width(xpLevel)) / 2;
		int y = background.getHeight() - 10;

		fontRenderer.draw(matrixStack, xpLevel, (float) x + 1, y, 0);
		fontRenderer.draw(matrixStack, xpLevel, (float) x - 1, y, 0);
		fontRenderer.draw(matrixStack, xpLevel, x, (float) y + 1, 0);
		fontRenderer.draw(matrixStack, xpLevel, x, (float) y - 1, 0);
		fontRenderer.draw(matrixStack, xpLevel, x, y, 8453920);
	}

	private void drawXpBar(PoseStack matrixStack, int experiencePoints, int level) {
		int partialXp = experiencePoints - XpHelper.getExperienceForLevel(level);
		int maxBarExperience = XpHelper.getExperienceLimitOnLevel(level);

		if (partialXp == 0) {
			return;
		}

		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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
		Matrix4f matrix = matrixStack.last().pose();
		bufferBuilder.vertex(matrix, x, y + height, 0.0F).uv(minU / textureWidth, maxV / textureHeight).endVertex();
		bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).uv(maxU / textureWidth, maxV / textureHeight).endVertex();
		bufferBuilder.vertex(matrix, x + width, y, 0.0F).uv(maxU / textureWidth, minV / textureHeight).endVertex();
		bufferBuilder.vertex(matrix, x, y, 0.0F).uv(minU / textureWidth, minV / textureHeight).endVertex();
		tesselator.end();

		RenderSystem.disableBlend();
	}
}
