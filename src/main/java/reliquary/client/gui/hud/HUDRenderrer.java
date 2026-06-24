package reliquary.client.gui.hud;

import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import reliquary.client.gui.components.Component;

public class HUDRenderrer {
	private HUDRenderrer() {
	}

	public static void render(GuiGraphicsExtractor guiGraphics, Component component, HUDPosition position) {
		if (component.shouldRender()) {
			Minecraft mc = Minecraft.getInstance();
			Window mainWindow = mc.getWindow();
			Pair<Integer, Integer> xy = getXYPosition(mainWindow, component, position);

			component.render(guiGraphics, xy.getFirst(), xy.getSecond());
		}
	}

	private static Pair<Integer, Integer> getXYPosition(Window sr, Component component, HUDPosition position) {
		return switch (position) {
			case BOTTOM_LEFT -> Pair.of(0, sr.getGuiScaledHeight() - component.getHeight());
			case LEFT -> Pair.of(0, (sr.getGuiScaledHeight() - component.getHeight()) / 2);
			case TOP_LEFT -> Pair.of(0, 0);
			case TOP -> Pair.of((sr.getGuiScaledWidth() - component.getWidth()) / 2, 0);
			case TOP_RIGHT -> Pair.of(sr.getGuiScaledWidth() - component.getWidth(), 0);
			case RIGHT -> Pair.of(sr.getGuiScaledWidth() - component.getWidth(), (sr.getGuiScaledHeight() - component.getHeight()) / 2);
			default -> Pair.of(sr.getGuiScaledWidth() - component.getWidth(), sr.getGuiScaledHeight() - component.getHeight());
		};
	}

}
