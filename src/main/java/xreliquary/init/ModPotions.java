package xreliquary.init;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xreliquary.reference.Reference;
import xreliquary.util.potions.PotionFlight;

public class ModPotions {
	public static Potion potionFlight = new PotionFlight();

	public static void init() {
		GameRegistry.register(potionFlight);
	}
}
