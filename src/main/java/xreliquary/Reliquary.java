package xreliquary;

import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xreliquary.client.ClientProxy;
import xreliquary.client.init.ModParticles;
import xreliquary.common.CommonProxy;
import xreliquary.crafting.AlkahestryRecipeRegistry;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModCompat;
import xreliquary.init.ModItems;
import xreliquary.init.PedestalItems;
import xreliquary.items.MobCharmRegistry;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionMap;

import static xreliquary.init.ModFluids.FLUIDS;

@Mod(Reference.MOD_ID)
public class Reliquary {
	public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public static final ItemGroup ITEM_GROUP = new ReliquaryItemGroup();

	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public Reliquary() {
		ForgeMod.enableMilkFluid();
		PROXY.registerHandlers();
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		FLUIDS.register(modBus);
		modBus.addListener(Reliquary::setup);
		modBus.addGenericListener(ParticleType.class, ModParticles::registerParticles);
		modBus.addListener(Reliquary::loadComplete);
		modBus.addListener(Settings::onFileChange);
		ModItems.registerListeners(modBus);
		ModBlocks.registerListeners(modBus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Settings.COMMON_SPEC);
		MinecraftForge.EVENT_BUS.addListener(MobCharmRegistry::handleAddingFragmentDrops);
		MinecraftForge.EVENT_BUS.addListener(AlkahestryRecipeRegistry::onResourceReload);
	}

	public static void setup(FMLCommonSetupEvent event) {
		ModItems.registerDispenseBehaviors();
		PotionMap.initPotionMap();
		ModCapabilities.init();
		PacketHandler.init();
		ModCompat.initCompats();
		ModCompat.setupCompats();
		ModItems.registerHandgunMagazines();
		PedestalItems.init();
	}

	public static void loadComplete(FMLLoadCompleteEvent event) {
		MobCharmRegistry.registerDynamicCharmDefinitions();
	}
}
