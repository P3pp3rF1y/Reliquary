package xreliquary.util.potions;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;

import java.util.Comparator;

public class EffectComparator implements Comparator<PotionEffect> {
	@Override
	public int compare(PotionEffect o1, PotionEffect o2) {
		int ret = I18n.translateToLocal(o1.getEffectName()).trim().compareTo(I18n.translateToLocal(o2.getEffectName()).trim());

		if(ret == 0)
			ret = Integer.compare(o1.getAmplifier(), o2.getAmplifier());

		if(ret == 0)
			ret = Integer.compare(o1.getDuration(), o2.getDuration());

		return ret;
	}
}
