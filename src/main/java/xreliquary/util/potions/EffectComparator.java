package xreliquary.util.potions;

import net.minecraft.potion.PotionEffect;
import xreliquary.util.LanguageHelper;

import java.util.Comparator;

public class EffectComparator implements Comparator<PotionEffect> {
	@Override
	public int compare(PotionEffect o1, PotionEffect o2) {
		int ret = LanguageHelper.getLocalization(o1.getEffectName()).trim().compareTo(LanguageHelper.getLocalization(o2.getEffectName()).trim());

		if(ret == 0)
			ret = Integer.compare(o1.getAmplifier(), o2.getAmplifier());

		if(ret == 0)
			ret = Integer.compare(o1.getDuration(), o2.getDuration());

		return ret;
	}
}
