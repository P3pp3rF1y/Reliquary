package xreliquary.util.potions;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

class WeightedPotionEffect extends PotionEffect {

	//the only constructor I care about, honestly. This takes the mapping and turns it into something usable.
	WeightedPotionEffect(String potionName, int duration, int amp) {
		//calls with amp of 0 (level 1)
		//potion effects are always ambient, it helps with stupid looking particles.
		//1.8.9 set the particles to not display
		super(Potion.getPotionFromResourceLocation(potionName), duration * 300, amp, true, false);
	}
}
