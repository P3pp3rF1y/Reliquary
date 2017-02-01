package xreliquary.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.util.LanguageHelper;

/**
 * A helper class for GUIs. Handles String parsing and positioning, and easily drawing ItemStacks.
 *
 * @author TheMike
 */
abstract class GuiBase extends GuiContainer {

	GuiBase(Container container) {
		super(container);
	}

	/**
	 * Draws a positioned set of strings, with a new line indicated by ';'.
	 *
	 * @param renderer The font renderer. Should be 'mc.fontRenderer' unless your using the Galactic fontRenderer.
	 * @param values   The String to parse. New line indicated by ';'. If the value is a localization key, it'll be translated and used.
	 * @param x        The x position of all of the lines.
	 * @param baseY    The base Y position. This will be modified by 9 each new line.
	 * @param color    The color value.
	 */
	void drawPositionedString(FontRenderer renderer, String values, int x, int baseY, int color) {
		if(!LanguageHelper.getLocalization(values).equals(values)) {
			values = LanguageHelper.getLocalization(values);
		}
		int count = 1;
		for(String value : values.split(";")) {
			int y = baseY + (count * renderer.FONT_HEIGHT);
			renderer.drawString(value, x + 15, y, color);
			count++;
		}
	}

	/**
	 * Not much different from drawPositionedString(), but this centers the String on the x axis.
	 *
	 * @param renderer The font renderer. Should be 'mc.fontRenderer' unless your using the Galactic fontRenderer.
	 * @param values   The String to parse. New line indicated by ';'. If the value is a localization key, it'll be translated and used.
	 * @param xLimit   The maximum x value allowed for centering.
	 * @param baseY    The base Y position. This will be modified by 9 each new line.
	 * @param color    The color value.
	 */
	void drawCenteredPositionedString(FontRenderer renderer, String values, int xLimit, int baseY, int color) {
		if(!LanguageHelper.getLocalization(values).equals(values)) {
			values = LanguageHelper.getLocalization(values);
		}
		int count = 1;
		for(String value : values.split(";")) {
			int x = (xLimit - renderer.getStringWidth(value)) / 2;
			int y = baseY + (count * renderer.FONT_HEIGHT);
			renderer.drawString(value, x + 15, y, color);
			count++;
		}
	}

	/**
	 * Binds the texture at location to the GL texture matrix.
	 *
	 * @param location The location of the texture.
	 */
	void bindTexture(ResourceLocation location) {
		this.mc.renderEngine.bindTexture(location);
	}

	/**
	 * Provides an easy way to draw an ItemStack into the inventory.
	 *
	 * @param stack The ItemStack to be drawn.
	 * @param x     Where the stack will be placed on the x axis.
	 * @param y     Where the stack will be placed on the y axis.
	 */
	void drawItemStack(ItemStack stack, int x, int y) {
		FontRenderer fr = stack.getItem().getFontRenderer(stack);
		if(fr == null)
			fr = fontRenderer;

		GlStateManager.disableLighting();
		this.zLevel = 200.0F;
		itemRender.zLevel = 200.0F;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		itemRender.renderItemOverlayIntoGUI(fr, stack, x, y, null);
		this.zLevel = 0.0F;
		itemRender.zLevel = 0.0F;
		GlStateManager.enableLighting();
	}

}
