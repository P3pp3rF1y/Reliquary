package reliquary.potions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import reliquary.init.ModEffects;

public class CureEffect extends MobEffect {

	public CureEffect() {
		super(MobEffectCategory.BENEFICIAL, 15723850);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int potency) {
		if (livingEntity instanceof ZombieVillager zombieVillager) {
			if (!zombieVillager.isConverting() && livingEntity.hasEffect(MobEffects.WEAKNESS)) {
				zombieVillager.startConverting(null, (livingEntity.level().getRandom().nextInt(2401) + 3600) / (potency + 2));
				livingEntity.removeEffect(ModEffects.CURE);
			}
			return true;
		} else {
			livingEntity.removeEffect(ModEffects.CURE);
			return false;
		}
	}
}
