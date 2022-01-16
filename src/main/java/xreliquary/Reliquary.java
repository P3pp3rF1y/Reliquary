package xreliquary;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xreliquary.client.init.ModParticles;
import xreliquary.crafting.AlkahestryRecipeRegistry;
import xreliquary.data.DataGenerators;
import xreliquary.handler.ClientEventHandler;
import xreliquary.handler.CommonEventHandler;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModCompat;
import xreliquary.init.ModEntities;
import xreliquary.init.ModItems;
import xreliquary.init.ModLoot;
import xreliquary.init.ModPotions;
import xreliquary.init.ModSounds;
import xreliquary.init.PedestalItems;
import xreliquary.items.MobCharmRegistry;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionMap;

import static xreliquary.init.ModFluids.FLUIDS;

@Mod(Reference.MOD_ID)
public class Reliquary {
	public static final CreativeModeTab ITEM_GROUP = new ReliquaryItemGroup();

	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public Reliquary() {
		ForgeMod.enableMilkFluid();
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientEventHandler.registerHandlers();
		}
		FLUIDS.register(modBus);
		modBus.addListener(Reliquary::setup);
		modBus.addListener(Reliquary::loadComplete);
		modBus.addListener(Settings::onFileChange);
		modBus.addListener(DataGenerators::gatherData);
		modBus.addGenericListener(ParticleType.class, ModParticles::registerParticles);

		ModItems.registerListeners(modBus);
		ModBlocks.registerListeners(modBus);
		ModEntities.registerListeners(modBus);
		ModCapabilities.registerListeners(modBus);
		ModPotions.registerListeners(modBus);
		ModSounds.registerListeners(modBus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Settings.COMMON_SPEC);

		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		CommonEventHandler.registerEventBusListeners(eventBus);
		eventBus.addListener(MobCharmRegistry::handleAddingFragmentDrops);
		eventBus.addListener(AlkahestryRecipeRegistry::onResourceReload);
		ModLoot.registerEventBusListeners(eventBus);

		ModCompat.initCompats();
	}

	public static void setup(FMLCommonSetupEvent event) {
		ModItems.registerDispenseBehaviors();
		PotionMap.initPotionMap();
		PacketHandler.init();
		ModItems.registerHandgunMagazines();
		PedestalItems.init();
	}

	public static void loadComplete(FMLLoadCompleteEvent event) {
		MobCharmRegistry.registerDynamicCharmDefinitions();
	}
}
