package reliquary.potions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import reliquary.util.MobHelper;

public class PacificationEffect extends MobEffect {

	public PacificationEffect() {
		super(MobEffectCategory.BENEFICIAL, 0);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int potency) {
		if (livingEntity.level().isClientSide || !(livingEntity instanceof Mob entityLiving)) {
			return false;
		}

		if (entityLiving.getTarget() != null || entityLiving.getLastHurtByMob() != null) {
			MobHelper.resetTarget(entityLiving, true);
		}
		return true;
	}
}
