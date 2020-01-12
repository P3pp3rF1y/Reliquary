package xreliquary.util.potions;

import net.minecraft.potion.EffectInstance;

import java.util.Comparator;

class EffectComparator implements Comparator<EffectInstance> {
	@Override
	public int compare(EffectInstance o1, EffectInstance o2) {
		int ret = o1.getEffectName().trim().compareTo(o2.getEffectName().trim());

		if(ret == 0) {
			ret = Integer.compare(o1.getAmplifier(), o2.getAmplifier());
		}

		if(ret == 0) {
			ret = Integer.compare(o1.getDuration(), o2.getDuration());
		}

		return ret;
	}
}
