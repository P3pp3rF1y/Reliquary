package reliquary.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * A helper class for GUIs. Handles String parsing and positioning, and easily drawing ItemStacks.
 *
 * @author TheMike
 */
abstract class BaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	BaseScreen(T container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}

	/**
	 * Provides an easy way to draw an ItemStack into the inventory.
	 *
	 * @param stack The ItemStack to be drawn.
	 * @param x     Where the stack will be placed on the x axis.
	 * @param y     Where the stack will be placed on the y axis.
	 */
	void drawItemStack(GuiGraphicsExtractor guiGraphics, ItemStack stack, int x, int y) {
		guiGraphics.item(stack, x, y);
		guiGraphics.itemDecorations(font, stack, x, y, null);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
	}
}
