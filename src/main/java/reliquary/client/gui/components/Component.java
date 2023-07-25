package reliquary.client.gui.components;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;

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

	public void render(GuiGraphics guiGraphics, int x, int y) {
		renderInternal(guiGraphics, x + getPadding(), y + getPadding());
	}

	public boolean shouldRender() {
		return true;
	}

	public abstract int getHeightInternal();

	public abstract int getWidthInternal();

	public abstract void renderInternal(GuiGraphics guiGraphics, int x, int y);

	protected void blit(int x, int y, int textureX, int textureY, int width, int height) {
		blit(x, y, textureX, textureY, width, height, 256, 256);
	}

	@SuppressWarnings("java:S107")
	protected void blit(int x, int y, int textureX, int textureY, int width, int height, float textureWidth, float textureHeight) {
		float minU = textureX / textureWidth;
		float maxU = (textureX + width) / textureWidth;
		float minV = textureY / textureHeight;
		float maxV = (textureY + height) / textureHeight;

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(x, (double) y + height, 0).uv(minU, maxV).endVertex();
		buffer.vertex((double) x + width, (double) y + height, 0).uv(maxU, maxV).endVertex();
		buffer.vertex((double) x + width, y, 0).uv(maxU, minV).endVertex();
		buffer.vertex(x, y, 0).uv(minU, minV).endVertex();
		tessellator.end();
	}
}
