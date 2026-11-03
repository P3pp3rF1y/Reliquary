package reliquary.util;

import net.minecraft.server.level.ServerLevel;
import reliquary.entity.ReliquaryFakePlayer;

import java.util.IdentityHashMap;
import java.util.Map;

public class FakePlayerFactory {
	private FakePlayerFactory() {
	}

	private static final Map<ServerLevel, ReliquaryFakePlayer> fakePlayers = new IdentityHashMap<>();

	public static ReliquaryFakePlayer get(ServerLevel level) {
		return fakePlayers.computeIfAbsent(level, ReliquaryFakePlayer::new);
	}

	public static void unloadWorld(ServerLevel level) {
		fakePlayers.remove(level);
	}

}
