package xreliquary.client.init;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.client.particle.CauldronBubbleParticle;
import xreliquary.client.particle.CauldronSteamParticle;
import xreliquary.client.particle.ColorParticleData;
import xreliquary.reference.Reference;
import xreliquary.util.InjectionHelper;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MOD_ID)
public class ModParticles {
	public static final ParticleType<ColorParticleData> CAULDRON_STEAM = InjectionHelper.nullValue();
	public static final ParticleType<ColorParticleData> CAULDRON_BUBBLE = InjectionHelper.nullValue();

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
		evt.getRegistry().register(new ParticleType<>(false, ColorParticleData.DESERIALIZER).setRegistryName(Reference.MOD_ID, "cauldron_steam"));
		evt.getRegistry().register(new ParticleType<>(false, ColorParticleData.DESERIALIZER).setRegistryName(Reference.MOD_ID, "cauldron_bubble"));
	}

	@SuppressWarnings("unused")
	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class FactoryHandler {
		@SubscribeEvent
		public static void registerFactories(ParticleFactoryRegisterEvent evt) {
			Minecraft.getInstance().particles.registerFactory(CAULDRON_STEAM, CauldronSteamParticle.Factory::new);
			Minecraft.getInstance().particles.registerFactory(CAULDRON_BUBBLE, CauldronBubbleParticle.Factory::new);
		}
	}
}
