package reliquary.client;

import net.minecraft.client.Minecraft;

public class ClientInputHelper {
	private ClientInputHelper() {
	}

	public static boolean isShiftKeyDown() {
		return Minecraft.getInstance().options.keyShift.isDown();
	}
}
