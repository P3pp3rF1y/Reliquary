package xreliquary.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

import java.util.Optional;

public class MobHelper {
	private MobHelper() {}

	public static void resetTarget(MobEntity entity) {
		resetTarget(entity, false);
	}

	public static void resetTarget(MobEntity entity, boolean resetRevengeTarget) {
		Brain<?> brain = entity.getBrain();
		brain.setMemory(MemoryModuleType.ATTACK_TARGET, Optional.empty());
		brain.setMemory(MemoryModuleType.ANGRY_AT, Optional.empty());
		brain.setMemory(MemoryModuleType.UNIVERSAL_ANGER, Optional.empty());
		entity.setAttackTarget(null);
		if (resetRevengeTarget) {
			entity.setRevengeTarget(null);
		}
	}
}
