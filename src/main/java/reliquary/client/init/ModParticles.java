package reliquary.client.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import reliquary.client.particle.CauldronBubbleParticle;
import reliquary.client.particle.CauldronBubbleParticleType;
import reliquary.client.particle.CauldronSteamParticle;
import reliquary.client.particle.CauldronSteamParticleType;
import reliquary.reference.Reference;

public class ModParticles {
	private ModParticles() {}

	private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MOD_ID);
	public static final RegistryObject<CauldronSteamParticleType> CAULDRON_STEAM = PARTICLES.register("cauldron_steam", CauldronSteamParticleType::new);
	public static final RegistryObject<CauldronBubbleParticleType> CAULDRON_BUBBLE = PARTICLES.register("cauldron_bubble", CauldronBubbleParticleType::new);

	public static void registerListeners(IEventBus modBus) {
		PARTICLES.register(modBus);
	}

	public static class ProviderHandler {
		private ProviderHandler() {}

		public static void registerProviders(RegisterParticleProvidersEvent event) {
			event.register(CAULDRON_STEAM.get(), CauldronSteamParticle.Provider::new);
			event.register(CAULDRON_BUBBLE.get(), CauldronBubbleParticle.Provider::new);
		}
	}
}
