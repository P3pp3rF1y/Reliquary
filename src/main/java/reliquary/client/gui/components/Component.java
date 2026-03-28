package reliquary.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;

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

	public void render(GuiGraphicsExtractor guiGraphics, int x, int y) {
		renderInternal(guiGraphics, x + getPadding(), y + getPadding());
	}

	public boolean shouldRender() {
		return true;
	}

	public abstract int getHeightInternal();

	public abstract int getWidthInternal();

	public abstract void renderInternal(GuiGraphicsExtractor guiGraphics, int x, int y);
}
