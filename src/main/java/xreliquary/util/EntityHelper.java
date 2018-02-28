package xreliquary.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;

public class EntityHelper {
	public static void removeNegativeStatusEffects(EntityLivingBase player) {
		player.removePotionEffect(MobEffects.WITHER);
		player.removePotionEffect(MobEffects.HUNGER);
		player.removePotionEffect(MobEffects.POISON);
		player.removePotionEffect(MobEffects.NAUSEA);
		player.removePotionEffect(MobEffects.MINING_FATIGUE);
		player.removePotionEffect(MobEffects.SLOWNESS);
		player.removePotionEffect(MobEffects.BLINDNESS);
		player.removePotionEffect(MobEffects.WEAKNESS);
	}
}
