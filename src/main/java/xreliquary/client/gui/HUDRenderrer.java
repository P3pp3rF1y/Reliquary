package xreliquary.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Tuple;
import org.lwjgl.opengl.GL11;
import xreliquary.client.gui.components.Component;

public class HUDRenderrer {
	public static void render(Component component, DisplayPosition position) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		Tuple<Integer, Integer>	xy = getXYPosition(sr, component, position);

		renderComponent(sr, xy.getFirst(), xy.getSecond(), component);

	}

	private static void renderComponent(ScaledResolution sr, int x, int y, Component component) {
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.clear(256);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();

		component.render(x, y);

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	private static Tuple<Integer, Integer> getXYPosition(ScaledResolution sr, Component component, DisplayPosition position) {
		switch(position) {
			case BOTTOM_LEFT:
				return new Tuple<>(0, sr.getScaledHeight() - component.getHeight());
			case LEFT:
				return new Tuple<>(0, (sr.getScaledHeight() - component.getHeight()) / 2);
			case TOP_LEFT:
				return new Tuple<>(0, 0);
			case TOP:
				return new Tuple<>((sr.getScaledWidth() - component.getWidth()) / 2, 0);
			case TOP_RIGHT:
				return new Tuple<>(sr.getScaledWidth() - component.getWidth(), 0);
			case RIGHT:
				return new Tuple<>(sr.getScaledWidth() - component.getWidth(), (sr.getScaledHeight() - component.getHeight()) / 2);
			case BOTTOM_RIGHT:
			default:
				return new Tuple<>(sr.getScaledWidth() - component.getWidth(), sr.getScaledHeight() - component.getHeight());
		}
	}

	public enum DisplayPosition {
		BOTTOM_LEFT,
		LEFT,
		TOP_LEFT,
		TOP,
		TOP_RIGHT,
		RIGHT,
		BOTTOM_RIGHT
	}
}
