package xreliquary.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * A helper class for GUIs. Handles String parsing and positioning, and easily drawing ItemStacks.
 *
 * @author TheMike
 */
abstract class GuiBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	GuiBase(T container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}

	/**
	 * Binds the texture at location to the GL texture matrix.
	 *
	 * @param location The location of the texture.
	 */
	void bindTexture(ResourceLocation location) {
		RenderSystem.setShaderTexture(0, location);
	}

	/**
	 * Provides an easy way to draw an ItemStack into the inventory.
	 *
	 * @param stack The ItemStack to be drawn.
	 * @param x     Where the stack will be placed on the x axis.
	 * @param y     Where the stack will be placed on the y axis.
	 */
	void drawItemStack(ItemStack stack, int x, int y) {
		setBlitOffset(200);
		itemRenderer.blitOffset = 200.0F;
		itemRenderer.renderAndDecorateItem(stack, x, y);
		itemRenderer.renderGuiItemDecorations(font, stack, x, y, null);
		setBlitOffset(0);
		itemRenderer.blitOffset = 0.0F;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}
