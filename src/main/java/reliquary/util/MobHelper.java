package reliquary.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;

public class MobHelper {
	private MobHelper() {}

	public static void resetTarget(Mob entity) {
		resetTarget(entity, false);
	}

	public static void resetTarget(Mob entity, boolean resetRevengeTarget) {
		Brain<?> brain = entity.getBrain();
		brain.setMemory(MemoryModuleType.ATTACK_TARGET, Optional.empty());
		brain.setMemory(MemoryModuleType.ANGRY_AT, Optional.empty());
		brain.setMemory(MemoryModuleType.UNIVERSAL_ANGER, Optional.empty());
		if (brain.hasMemoryValue(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD)) {
			brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, Optional.empty());
		}
		entity.setTarget(null);
		if (resetRevengeTarget) {
			entity.setLastHurtByMob(null);
		}
	}
}
