package xreliquary.util;

import net.minecraft.entity.MobEntity;

public class MobHelper {
	private MobHelper() {}

	public static void resetTarget(MobEntity entity) {
		resetTarget(entity, false);
	}

	public static void resetTarget(MobEntity entity, boolean resetRevengeTarget) {
		entity.setAttackTarget(null);
		if (resetRevengeTarget) {
			entity.setRevengeTarget(null);
		}
	}
}
