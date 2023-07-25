package reliquary.util;

import net.minecraft.server.level.ServerLevel;
import reliquary.entities.EntityXRFakePlayer;

public class XRFakePlayerFactory {
	private XRFakePlayerFactory() {}

	private static EntityXRFakePlayer fakePlayer;

	public static EntityXRFakePlayer get(ServerLevel world) {
		if (fakePlayer == null) {
			fakePlayer = new EntityXRFakePlayer(world);
		}

		return fakePlayer;
	}

	public static void unloadWorld(ServerLevel world) {
		if (fakePlayer != null && fakePlayer.level() == world) {
			fakePlayer = null;
		}
	}

}
