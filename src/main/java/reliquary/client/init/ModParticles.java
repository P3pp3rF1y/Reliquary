package reliquary.client.init;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import reliquary.Reliquary;
import reliquary.client.particle.CauldronBubbleParticle;
import reliquary.client.particle.CauldronBubbleParticleType;
import reliquary.client.particle.CauldronSteamParticle;
import reliquary.client.particle.CauldronSteamParticleType;

import java.util.function.Supplier;

public class ModParticles {
	private ModParticles() {
	}

	private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Reliquary.MOD_ID);
	public static final Supplier<ParticleType<ColorParticleOption>> CAULDRON_STEAM = PARTICLE_TYPES.register("cauldron_steam", CauldronSteamParticleType::new);
	public static final Supplier<ParticleType<ColorParticleOption>> CAULDRON_BUBBLE = PARTICLE_TYPES.register("cauldron_bubble",
			CauldronBubbleParticleType::new);

	public static void registerListeners(IEventBus modBus) {
		PARTICLE_TYPES.register(modBus);
	}

	public static class ProviderHandler {
		private ProviderHandler() {
		}

		public static void registerProviders(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(CAULDRON_STEAM.get(), CauldronSteamParticle.Provider::new);
			event.registerSpriteSet(CAULDRON_BUBBLE.get(), CauldronBubbleParticle.Provider::new);
		}
	}
}
