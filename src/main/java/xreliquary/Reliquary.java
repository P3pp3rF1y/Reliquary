package xreliquary;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xreliquary.client.ClientProxy;
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
	public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public static final ItemGroup ITEM_GROUP = new ReliquaryItemGroup();

	public Reliquary() {
		proxy.registerHandlers();
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		FLUIDS.register(eventBus);
		eventBus.addListener(Reliquary::setup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Settings.COMMON_SPEC);
		proxy.registerHandlers();
	}

	public static void setup(FMLCommonSetupEvent event) {
		ModItems.registerDispenseBehaviors();
		PotionMap.initPotionMap();
		ModCapabilities.init();
		PacketHandler.init();
		ModCompat.registerModCompat();
		ModCompat.loadCompats();
		ModItems.registerHandgunMagazines();
		PedestalItems.init();
	}
}
