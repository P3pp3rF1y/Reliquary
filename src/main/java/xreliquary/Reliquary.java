package xreliquary;

import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xreliquary.client.ClientProxy;
import xreliquary.client.init.ModParticles;
import xreliquary.common.CommonProxy;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModCompat;
import xreliquary.init.ModItems;
import xreliquary.init.PedestalItems;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.potions.PotionMap;

import static xreliquary.init.ModFluids.FLUIDS;

@Mod(Reference.MOD_ID)
public class Reliquary {
	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public static final ItemGroup ITEM_GROUP = new ReliquaryItemGroup();

	public Reliquary() {
		proxy.registerHandlers();
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		FLUIDS.register(modBus);
		modBus.addListener(Reliquary::setup);
		modBus.addGenericListener(ParticleType.class, ModParticles::registerParticles);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Settings.COMMON_SPEC);
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
}
