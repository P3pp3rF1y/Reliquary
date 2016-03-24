package xreliquary.init;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.util.potions.PotionFlight;

public class ModPotions {
	public static Potion potionFlight;

	public static void init() {
		potionFlight = new PotionFlight();
	}
}
