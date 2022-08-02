package reliquary.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.Set;

public class MobHelper {
	private MobHelper() {}

	private static final Set<MemoryModuleType<?>> TARGET_MEMORIES = ImmutableSet.of(
			MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER,
			MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER
	);

	public static void resetTarget(Mob entity) {
		resetTarget(entity, false);
	}

	public static void resetTarget(Mob entity, boolean resetRevengeTarget) {
		Brain<?> brain = entity.getBrain();
		for (var memory : TARGET_MEMORIES) {
			if (brain.hasMemoryValue(memory)) {
				emptyMemory(brain, memory);
			}
		}

		entity.setTarget(null);
		if (resetRevengeTarget) {
			entity.setLastHurtByMob(null);
		}
	}

	private static <T> void emptyMemory(Brain<?> brain, MemoryModuleType<T> memory) {
		brain.setMemory(memory, Optional.empty());
	}

	public static Optional<Player> getTargetedPlayerFromMemory(Mob entity) {
		Brain<?> brain = entity.getBrain();
		for (var memory : TARGET_MEMORIES) {
			if (brain.hasMemoryValue(memory)) {
				Optional<?> value = brain.getMemory(memory);
				if (value.isPresent() && value.get() instanceof Player) {
					return Optional.of((Player) value.get());
				}
			}
		}
		return Optional.empty();
	}
}
