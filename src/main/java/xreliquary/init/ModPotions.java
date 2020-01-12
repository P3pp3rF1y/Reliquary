package xreliquary.init;

import net.minecraft.potion.Effect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xreliquary.potions.PotionCure;
import xreliquary.potions.PotionFlight;
import xreliquary.potions.PotionPacification;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModPotions {
	public static Effect potionFlight;
	public static Effect potionPacification;
	public static Effect potionCure;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Effect> event) {
		potionFlight = new PotionFlight();
		potionPacification = new PotionPacification();
		potionCure = new PotionCure();

		event.getRegistry().register(potionFlight);
		event.getRegistry().register(potionPacification);
		event.getRegistry().register(potionCure);
	}
}
