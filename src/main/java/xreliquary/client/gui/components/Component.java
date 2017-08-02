package xreliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public abstract class Component {
	public int getPadding() {
		return 1;
	}

	public int getHeight() {
		return getHeightInternal() + getPadding() * 2;
	}

	public int getWidth() {
		return getWidthInternal() + getPadding() * 2;
	}

	public void render(int x, int y) {
		// drawTexturedModalRect(x, y, 5, 170, getWidth(), getHeight());
		if (hasChanged()) {
			refresh();
			setChanged(false);
		}
		renderInternal(x + getPadding(), y + getPadding());
	}

	protected void refresh() {
	}

	public boolean hasChanged() {
		return false;
	}

	public void setChanged(boolean changed) {
	}

	public boolean shouldRender() {
		return true;
	}

	public abstract int getHeightInternal();
	public abstract int getWidthInternal();
	public abstract void renderInternal(int x, int y);

	private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(GuiContainer.INVENTORY_BACKGROUND);
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		double z = 0.0D;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), z).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), z).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), z).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), z).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}
}
