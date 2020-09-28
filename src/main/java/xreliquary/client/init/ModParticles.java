package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.client.particle.BubbleColorParticleData;
import xreliquary.client.particle.CauldronBubbleParticle;
import xreliquary.client.particle.CauldronBubbleParticleType;
import xreliquary.client.particle.CauldronSteamParticle;
import xreliquary.client.particle.CauldronSteamParticleType;
import xreliquary.client.particle.SteamColorParticleData;
import xreliquary.reference.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class ModParticles {
	private ModParticles() {}
	public static final ParticleType<SteamColorParticleData> CAULDRON_STEAM = new CauldronSteamParticleType();
	public static final ParticleType<BubbleColorParticleData> CAULDRON_BUBBLE = new CauldronBubbleParticleType();

	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
		evt.getRegistry().register(CAULDRON_STEAM.setRegistryName(Reference.MOD_ID, "cauldron_steam"));
		evt.getRegistry().register(CAULDRON_BUBBLE.setRegistryName(Reference.MOD_ID, "cauldron_bubble"));
	}

	public static class FactoryHandler {
		private FactoryHandler() {}

		public static void registerFactories(ParticleFactoryRegisterEvent event) {
			Minecraft.getInstance().particles.registerFactory(CAULDRON_STEAM, CauldronSteamParticle.Factory::new);
			Minecraft.getInstance().particles.registerFactory(CAULDRON_BUBBLE, CauldronBubbleParticle.Factory::new);
		}
	}
}
