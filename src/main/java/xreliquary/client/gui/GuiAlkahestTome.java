package xreliquary.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xreliquary.common.gui.ContainerAlkahestTome;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class GuiAlkahestTome extends GuiBase<ContainerAlkahestTome> {
	private static final ResourceLocation BOOK_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/book.png");

	public GuiAlkahestTome(ContainerAlkahestTome container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		//noinspection ConstantConditions
		drawTitleText(minecraft.getFontResourceManager().getFontRenderer(Minecraft.standardGalacticFontRenderer));
		drawTomeText(font);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		bindTexture(BOOK_TEX);
		blit((width - 146) / 2, (height - 179) / 2, 0, 0, 146, 179);
		blit(((width - 16) / 2) + 19, ((height - 179) / 2) + 148, 0, 180, 10, 10);
		blit(((width - 16) / 2) - 14, ((height - 179) / 2) + 148, 10, 180, 10, 10);

		drawItemStack(new ItemStack(ModItems.ALKAHESTRY_TOME), (width - 16) / 2, ((height - 179) / 2) + 145);
		drawItemStack(XRRecipes.drainRecipe.getRecipeOutput(), ((width - 16) / 2) - 32, ((height - 179) / 2) + 145);
		drawItemStack(XRRecipes.drainRecipe.getRecipeOutput(), ((width - 16) / 2) + 32, ((height - 179) / 2) + 145);
	}

	private void drawTomeText(FontRenderer renderer) {
		String values = "gui.xreliquary.alkahestry_tome.text";
		if (!LanguageHelper.getLocalization(values).equals(values)) {
			values = LanguageHelper.getLocalization(values);
		}
		int count = 1;
		for (String value : values.split(";")) {
			int y = 36 + (count * renderer.FONT_HEIGHT);
			renderer.drawString(value, (float) 16 + 15, y, 0);
			count++;
		}
	}

	private void drawTitleText(FontRenderer renderer) {
		String values = "Perform basic,;intermediate or;advanced Alkahestry.";
		if (!LanguageHelper.getLocalization(values).equals(values)) {
			values = LanguageHelper.getLocalization(values);
		}
		int count = 1;
		for (String value : values.split(";")) {
			int x = (146 - renderer.getStringWidth(value)) / 2;
			int y = 4 + (count * renderer.FONT_HEIGHT);
			renderer.drawString(value, (float) x + 15, y, 0);
			count++;
		}
	}
}
