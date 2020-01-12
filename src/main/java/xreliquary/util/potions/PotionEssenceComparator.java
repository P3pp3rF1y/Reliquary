package xreliquary.util.potions;

import java.util.Comparator;

public class PotionEssenceComparator implements Comparator<PotionEssence> {
	@Override
	public int compare(PotionEssence o1, PotionEssence o2) {

		int ret = 0;

		for(int i = 0; i < Math.min(o1.getEffects().size(), o2.getEffects().size()); i++) {
			ret = new EffectComparator().compare(o1.getEffects().get(i), o2.getEffects().get(i));
			if(ret != 0) {
				break;
			}
		}

		if(ret == 0) {
			ret = Integer.compare(o1.getEffects().size(), o2.getEffects().size());
		}

		return ret;
	}
}
