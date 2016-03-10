package xreliquary.util;

import net.minecraft.world.WorldServer;
import xreliquary.entities.EntityXRFakePlayer;

public class XRFakePlayerFactory {
	private static EntityXRFakePlayer fakePlayer;

	public static EntityXRFakePlayer get(WorldServer world) {
		if(fakePlayer == null)
			fakePlayer = new EntityXRFakePlayer(world);

		return fakePlayer;
	}

	public static void unloadWorld(WorldServer world) {
		if(fakePlayer != null && fakePlayer.worldObj == world)
			fakePlayer = null;
	}

}
