package xreliquary.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class MobHelper {
	private MobHelper() {}

	public static void resetTarget(MobEntity entity) {
		resetTarget(entity, false, false);
	}

	public static void resetTarget(MobEntity entity, boolean processPigmanLogic, boolean resetRevengeTarget) {
		entity.setAttackTarget(null);
		if (resetRevengeTarget) {
			entity.setRevengeTarget(null);
		}
		if(processPigmanLogic && entity instanceof ZombiePigmanEntity) {
			//need to reset ai task because it doesn't get reset with setAttackTarget or setRevengeTarget and keeps player as target
			entity.targetSelector.getRunningGoals().filter(prioritizedGoal -> prioritizedGoal.getGoal() instanceof HurtByTargetGoal).findFirst()
					.ifPresent(prioritizedGoal -> prioritizedGoal.getGoal().resetTask());

			//also need to reset anger target because apparently setRevengeTarget doesn't set this to null
			resetAngerTarget((ZombiePigmanEntity) entity);
		}
	}

	private static final Field ANGER_TARGET_UUID = ObfuscationReflectionHelper.findField(ZombiePigmanEntity.class, "field_175459_bn");
	@SuppressWarnings("squid:S3011")
	private static void resetAngerTarget(ZombiePigmanEntity zombiePigman) {
		try {
			ANGER_TARGET_UUID.set(zombiePigman, null);
		} catch (IllegalAccessException e) {
			LogHelper.error("Error setting angerTargetUUID to null in ZombiePigmanEntity", e);
		}
	}
}
