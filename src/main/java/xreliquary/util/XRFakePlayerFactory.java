package xreliquary.util;

import net.minecraft.world.server.ServerWorld;
import xreliquary.entities.EntityXRFakePlayer;

public class XRFakePlayerFactory {
	private static EntityXRFakePlayer fakePlayer;

	public static EntityXRFakePlayer get(ServerWorld world) {
		if(fakePlayer == null)
			fakePlayer = new EntityXRFakePlayer(world);

		return fakePlayer;
	}

	public static void unloadWorld(ServerWorld world) {
		if(fakePlayer != null && fakePlayer.world == world)
			fakePlayer = null;
	}

}
