package xreliquary.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * A helper class for GUIs. Handles String parsing and positioning, and easily drawing ItemStacks.
 *
 * @author TheMike
 */
abstract class GuiBase<T extends Container> extends ContainerScreen<T> {

	GuiBase(T container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}

	/**
	 * Binds the texture at location to the GL texture matrix.
	 *
	 * @param location The location of the texture.
	 */
	void bindTexture(ResourceLocation location) {
		//noinspection ConstantConditions - at the point this is used the instance is not null
		minecraft.textureManager.bindTexture(location);
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
		if (fr == null) {
			fr = font;
		}

		GlStateManager.disableLighting();
		blitOffset = 200;
		itemRenderer.zLevel = 200.0F;
		itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
		itemRenderer.renderItemOverlayIntoGUI(fr, stack, x, y, null);
		blitOffset = 0;
		itemRenderer.zLevel = 0.0F;
		GlStateManager.enableLighting();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
	}
}
