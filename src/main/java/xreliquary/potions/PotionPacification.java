package xreliquary.potions;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import xreliquary.reference.Reference;
import xreliquary.util.MobHelper;

public class PotionPacification extends Effect {

	public PotionPacification() {
		super(EffectType.BENEFICIAL, 0);
		setRegistryName(Reference.MOD_ID, "pacification");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect( LivingEntity entityLivingBase, int amplifier) {
		if (entityLivingBase.world.isRemote || !(entityLivingBase instanceof MobEntity)) {
			return;
		}

		MobEntity entityLiving = (MobEntity) entityLivingBase;

		if (entityLiving.getAttackTarget() != null || entityLiving.getRevengeTarget() != null) {
			MobHelper.resetTarget(entityLiving, true);
		}
	}
}
