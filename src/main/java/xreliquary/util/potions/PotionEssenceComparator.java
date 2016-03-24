package xreliquary.util.potions;

import java.util.Comparator;

public class PotionEssenceComparator implements Comparator<PotionEssence> {
	@Override
	public int compare(PotionEssence o1, PotionEssence o2) {

		int ret = 0;

		for(int i = 0; i < Math.min(o1.effects.size(), o2.effects.size()); i++) {
			ret = new EffectComparator().compare(o1.effects.get(i), o2.effects.get(i));
			if(ret != 0)
				break;
		}

		if(ret == 0)
			ret = Integer.compare(o1.effects.size(), o2.effects.size());

		return ret;
	}
}
