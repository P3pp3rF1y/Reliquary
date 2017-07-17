package xreliquary.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class MobHelper {

	public static void resetTarget(EntityLiving entity) {
		resetTarget(entity, false);
	}

	public static void resetTarget(EntityLiving entity, boolean processPigmanLogic) {
		entity.setAttackTarget(null);
		entity.setRevengeTarget(null);
		if(processPigmanLogic && entity instanceof EntityPigZombie) {
			//need to reset ai task because it doesn't get reset with setAttackTarget or setRevengeTarget and keeps player as target
			for (EntityAITasks.EntityAITaskEntry aiTask : entity.targetTasks.taskEntries) {
				if (aiTask.action instanceof EntityAIHurtByTarget) {
					aiTask.action.resetTask();
					break;
				}
			}

			//also need to reset anger target because apparently setRevengeTarget doesn't set this to null
			resetAngerTarget((EntityPigZombie) entity);
		}
	}

	private static final Field SET_ANGER_TARGET = ReflectionHelper.findField(EntityPigZombie.class, "field_175459_bn", "angerTargetUUID");
	private static void resetAngerTarget(EntityPigZombie zombiePigman) {
		try {
			SET_ANGER_TARGET.set(zombiePigman, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
