package reliquary.util;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EntityHelper {
	public static void removeNegativeStatusEffects(LivingEntity player) {
		List<Holder<MobEffect>> negativeEffects = player.getActiveEffects()
				.stream()
				.map(MobEffectInstance::getEffect)
				.filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
				.toList();
		negativeEffects.forEach(player::removeEffect);
	}
}
