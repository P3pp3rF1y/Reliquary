package xreliquary.util;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EntityHelper {
	public static void removeNegativeStatusEffects(LivingEntity player) {
		player.removeEffect(MobEffects.WITHER);
		player.removeEffect(MobEffects.HUNGER);
		player.removeEffect(MobEffects.POISON);
		player.removeEffect(MobEffects.CONFUSION);
		player.removeEffect(MobEffects.DIG_SLOWDOWN);
		player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
		player.removeEffect(MobEffects.BLINDNESS);
		player.removeEffect(MobEffects.WEAKNESS);
	}
}
