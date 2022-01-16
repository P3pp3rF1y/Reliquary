package xreliquary.util.potions;

import net.minecraft.world.effect.MobEffectInstance;

import java.util.Comparator;

class EffectComparator implements Comparator<MobEffectInstance> {
	@Override
	public int compare(MobEffectInstance o1, MobEffectInstance o2) {
		int ret = o1.getDescriptionId().trim().compareTo(o2.getDescriptionId().trim());

		if(ret == 0) {
			ret = Integer.compare(o1.getAmplifier(), o2.getAmplifier());
		}

		if(ret == 0) {
			ret = Integer.compare(o1.getDuration(), o2.getDuration());
		}

		return ret;
	}
}
