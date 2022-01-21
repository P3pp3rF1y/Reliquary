package reliquary;

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
import reliquary.client.init.ModParticles;
import reliquary.crafting.AlkahestryRecipeRegistry;
import reliquary.data.DataGenerators;
import reliquary.handler.ClientEventHandler;
import reliquary.handler.CommonEventHandler;
import reliquary.init.ModBlocks;
import reliquary.init.ModCapabilities;
import reliquary.init.ModCompat;
import reliquary.init.ModEnchantments;
import reliquary.init.ModEntities;
import reliquary.init.ModItems;
import reliquary.init.ModLoot;
import reliquary.init.ModPotions;
import reliquary.init.ModSounds;
import reliquary.init.PedestalItems;
import reliquary.items.MobCharmRegistry;
import reliquary.network.PacketHandler;
import reliquary.reference.Reference;
import reliquary.reference.Settings;
import reliquary.util.potions.PotionMap;

import static reliquary.init.ModFluids.FLUIDS;

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
		ModEnchantments.register(modBus);
		ModLoot.registerListeners(modBus);

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
