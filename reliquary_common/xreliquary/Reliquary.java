package xreliquary;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import xreliquary.blocks.XRBlocks;
import xreliquary.common.CommonProxy;
import xreliquary.items.AlkahestMap;
import xreliquary.items.XRAlkahestry;
import xreliquary.items.XRItems;
import xreliquary.lib.Reference;
import xreliquary.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class Reliquary {
	@Instance(Reference.MOD_ID)
	public static Reliquary instance;
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy proxy;
	public static CreativeTabs tabsXR = new CreativeTabXR(CreativeTabs.getNextID(), Reference.MOD_ID);

	@ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		// Initialize the custom commands
	}

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// Initialize the log helper
		LogHelper.init();
		Config.init(event.getSuggestedConfigurationFile());
		proxy.registerSoundHandler();
		proxy.registerTickHandlers();
		XRItems.init();
		XRAlkahestry.init();
		AlkahestMap.init();
		XRItems.registerLiquidContainer();
		XRBlocks.init();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		proxy.registerEntityTrackers();
		proxy.registerRenderers();
		proxy.registerEvents();
		proxy.registerTileEntities();
		LanguageRegistry.instance().addStringLocalization("itemGroup." + Reference.MOD_ID, "Xeno's Reliquary");
	}

	@PostInit
	public void modsLoaded(FMLPostInitializationEvent event) {
		System.out.println("Xeno's Reliquary loaded.");
	}

	public static void customBusterExplosion(Entity par1Entity, EntityPlayer player, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
		if (par1Entity.worldObj.isRemote) return;
		par1Entity.worldObj.newExplosion(par1Entity, par2, par4, par6, par8, par9, par10);
	}

	public static ConcussiveExplosion customConcussiveExplosion(Entity par1Entity, EntityPlayer player, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
		ConcussiveExplosion var11 = new ConcussiveExplosion(par1Entity.worldObj, par1Entity, player, par2, par4, par6, par8);
		var11.isFlaming = par9;
		var11.isSmoking = par10;
		var11.doExplosionA();
		var11.doExplosionB(false);
		return var11;
	}
}