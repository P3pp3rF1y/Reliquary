package reliquary.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import reliquary.util.MobHelper;

public class PacificationPotion extends MobEffect {

	public PacificationPotion() {
		super(MobEffectCategory.BENEFICIAL, 0);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entityLivingBase, int amplifier) {
		if (entityLivingBase.level.isClientSide || !(entityLivingBase instanceof Mob entityLiving)) {
			return;
		}

		if (entityLiving.getTarget() != null || entityLiving.getLastHurtByMob() != null) {
			MobHelper.resetTarget(entityLiving, true);
		}
	}
}
