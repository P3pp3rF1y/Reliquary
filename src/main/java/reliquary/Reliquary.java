package reliquary;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import reliquary.client.init.ModParticles;
import reliquary.crafting.AlkahestryRecipeRegistry;
import reliquary.data.DataGenerators;
import reliquary.handler.ClientEventHandler;
import reliquary.handler.CommonEventHandler;
import reliquary.init.*;
import reliquary.item.MobCharmRegistry;
import reliquary.reference.Config;
import reliquary.util.potions.PotionMap;

@Mod(Reliquary.MOD_ID)
public class Reliquary {

	public static final String MOD_ID = "reliquary";


	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public Reliquary(IEventBus modBus, Dist dist, ModContainer container) {
		NeoForgeMod.enableMilkFluid();
		if (dist == Dist.CLIENT) {
			ClientEventHandler.registerHandlers(container);
		}
		modBus.addListener(Reliquary::setup);
		modBus.addListener(Reliquary::loadComplete);
		modBus.addListener(Config::onFileChange);
		modBus.addListener(DataGenerators::gatherData);
		modBus.addListener(ModPayloads::registerPackets);

		ModFluids.registerHandlers(modBus);
		ModItems.registerListeners(modBus);
		ModBlocks.registerListeners(modBus);
		ModEntities.registerListeners(modBus);
		ModEffects.registerListeners(modBus);
		ModSounds.registerListeners(modBus);
		ModParticles.registerListeners(modBus);
		ModDataComponents.register(modBus);

		container.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
		container.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);

		IEventBus eventBus = NeoForge.EVENT_BUS;
		CommonEventHandler.registerEventBusListeners(eventBus);
		eventBus.addListener(MobCharmRegistry::handleAddingFragmentDrops);
		eventBus.addListener(AlkahestryRecipeRegistry::onResourceReload);

		ModCompat.initCompats(modBus);
	}

	public static void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(ModItems::registerDispenseBehaviors);
		PotionMap.initPotionMap();
		ModItems.registerHandgunMagazines();
		PedestalItems.init();
	}

	public static void loadComplete(FMLLoadCompleteEvent event) {
		MobCharmRegistry.registerDynamicCharmDefinitions();
	}

	public static ResourceLocation getRL(String regName) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, regName);
	}
}
