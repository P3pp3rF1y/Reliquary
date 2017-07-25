package xreliquary.init;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.potions.PotionCure;
import xreliquary.potions.PotionFlight;
import xreliquary.potions.PotionPacification;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ModPotions {
	public static Potion potionFlight;
	public static Potion potionPacification;
	public static Potion potionCure;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Potion> event) {
		potionFlight = new PotionFlight();
		potionPacification = new PotionPacification();
		potionCure = new PotionCure();

		event.getRegistry().register(potionFlight);
		event.getRegistry().register(potionPacification);
		event.getRegistry().register(potionCure);
	}
}
