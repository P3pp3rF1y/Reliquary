package xreliquary.client.gui.components;

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
		//Minecraft.getInstance().textureManager.bindTexture(GuiContainer.INVENTORY_BACKGROUND);
		//blit(x, y, 5, 170, getWidth(), getHeight());
		renderInternal(x + getPadding(), y + getPadding());
	}

	public boolean shouldRender() {
		return true;
	}

	public abstract int getHeightInternal();

	public abstract int getWidthInternal();

	public abstract void renderInternal(int x, int y);

	protected void blit(int x, int y, int textureX, int textureY, int width, int height) {
		blit(x, y, textureX, textureY, width, height, 256, 256);
	}

	protected void blit(int x, int y, int textureX, int textureY, int width, int height, double textureWidth, double textureHeight) {
		double minU = textureX / textureWidth;
		double maxU = (textureX + width) / textureWidth;
		double minV = textureY / textureHeight;
		double maxV = (textureY + height) / textureHeight;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x, y + height, 0).tex(minU, maxV).endVertex();
		buffer.pos(x + width, y + height, 0).tex(maxU, maxV).endVertex();
		buffer.pos(x + width, y, 0).tex(maxU, minV).endVertex();
		buffer.pos(x, y, 0).tex(minU, minV).endVertex();
		tessellator.draw();
	}
}
