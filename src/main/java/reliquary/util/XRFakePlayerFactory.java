package reliquary.util;

import net.minecraft.server.level.ServerLevel;
import reliquary.entity.EntityXRFakePlayer;

import java.util.IdentityHashMap;
import java.util.Map;

public class XRFakePlayerFactory {
	private XRFakePlayerFactory() {
	}

	private static final Map<ServerLevel, EntityXRFakePlayer> fakePlayers = new IdentityHashMap<>();

	public static EntityXRFakePlayer get(ServerLevel world) {
		return fakePlayers.computeIfAbsent(world, EntityXRFakePlayer::new);
	}

	public static void unloadWorld(ServerLevel world) {
		fakePlayers.remove(world);
	}

}
