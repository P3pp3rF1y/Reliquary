package xreliquary.client.gui.hud;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Tuple;
import xreliquary.client.gui.components.Component;

public class HUDRenderrer {
	private HUDRenderrer() {}
	public static void render(Component component, HUDPosition position) {
		if (component.shouldRender()) {
			Minecraft mc = Minecraft.getInstance();
			MainWindow mainWindow = mc.getMainWindow();
			Tuple<Integer, Integer> xy = getXYPosition(mainWindow, component, position);

			component.render(xy.getA(), xy.getB());
		}
	}

	private static Tuple<Integer, Integer> getXYPosition(MainWindow sr, Component component, HUDPosition position) {
		switch (position) {
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

}
