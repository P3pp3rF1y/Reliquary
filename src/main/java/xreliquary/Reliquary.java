package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import xreliquary.common.CommonProxy;
import xreliquary.compat.ICompat;
import xreliquary.init.ModCapabilities;
import xreliquary.init.ModCompat;
import xreliquary.init.ModFluids;
import xreliquary.init.ModLoot;
import xreliquary.init.PedestalItems;
import xreliquary.network.PacketHandler;
import xreliquary.pedestal.PedestalRegistry;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;
import xreliquary.util.potions.PotionMap;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "after:" + Compatibility.MOD_ID.BAUBLES)
public class Reliquary {

	@Instance(Reference.MOD_ID)
	public static Reliquary INSTANCE;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy PROXY;

	public static CreativeTabs CREATIVE_TAB = new CreativeTabXR(CreativeTabs.getNextID());

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		PROXY.initColors();

		ModFluids.preInit();

		ModCapabilities.init();

		PROXY.preInit();

		PacketHandler.init();

		ModCompat.registerModCompat();
		ModCompat.loadCompat(ICompat.InitializationPhase.PRE_INIT, null);
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void init(FMLInitializationEvent event) {

		ModLoot.init();

		PotionMap.initPotionMap();

		PROXY.initSpecialJEIDescriptions();

		ModFluids.init();

		PROXY.init();

		MinecraftForge.EVENT_BUS.register(this);

		ModCompat.loadCompat(ICompat.InitializationPhase.INIT, null);
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		PROXY.postInit();

		ModCompat.loadCompat(ICompat.InitializationPhase.POST_INIT, null);

		PedestalItems.init();

		LogHelper.info("Loaded successfully!");
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		PedestalRegistry.clearPositions();
	}
}
