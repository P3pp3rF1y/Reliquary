package xreliquary.init;

import net.minecraft.potion.Potion;
import xreliquary.util.potions.PotionFlight;

public class ModPotions {
	public static Potion potionFlight;

	public static void init() {
		potionFlight = new PotionFlight();
	}
}
