package xreliquary.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;

public class EntityHelper {
	public static void removeNegativeStatusEffects(LivingEntity player) {
		player.removePotionEffect(Effects.WITHER);
		player.removePotionEffect(Effects.HUNGER);
		player.removePotionEffect(Effects.POISON);
		player.removePotionEffect(Effects.NAUSEA);
		player.removePotionEffect(Effects.MINING_FATIGUE);
		player.removePotionEffect(Effects.SLOWNESS);
		player.removePotionEffect(Effects.BLINDNESS);
		player.removePotionEffect(Effects.WEAKNESS);
	}
}
