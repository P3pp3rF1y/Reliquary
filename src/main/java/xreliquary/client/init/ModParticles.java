package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import xreliquary.client.particle.BubbleColorParticleData;
import xreliquary.client.particle.CauldronBubbleParticle;
import xreliquary.client.particle.CauldronBubbleParticleType;
import xreliquary.client.particle.CauldronSteamParticle;
import xreliquary.client.particle.CauldronSteamParticleType;
import xreliquary.client.particle.SteamColorParticleData;
import xreliquary.reference.Reference;

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
			Minecraft.getInstance().particleEngine.register(CAULDRON_STEAM, CauldronSteamParticle.Factory::new);
			Minecraft.getInstance().particleEngine.register(CAULDRON_BUBBLE, CauldronBubbleParticle.Factory::new);
		}
	}
}
