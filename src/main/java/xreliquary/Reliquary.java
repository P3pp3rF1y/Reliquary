package xreliquary;

import java.util.logging.Level;

import com.google.common.collect.ImmutableList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import xreliquary.blocks.XRBlocks;
import xreliquary.common.CommonProxy;
import xreliquary.items.AlkahestMap;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.items.XRAlkahestry;
import xreliquary.items.XRItems;
import xreliquary.lib.Reference;
import xreliquary.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
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

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Initialize the custom commands
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	
        LogHelper.init();
        Config.init(event.getSuggestedConfigurationFile());

        proxy.registerSoundHandler();
        proxy.registerTickHandlers();

        XRItems.init();
        XRAlkahestry.init();
        AlkahestMap.init();

        FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME / 8), new ItemStack(XRItems.condensedPotion), XRItems.potion(Reference.WATER_META));
        
        XRBlocks.init();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.registerEntityTrackers();
        proxy.registerRenderers();
        MinecraftForge.EVENT_BUS.register(this);
        proxy.registerTileEntities();
        LanguageRegistry.instance().addStringLocalization(
                "itemGroup." + Reference.MOD_ID, "Xeno's Reliquary");
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent event) {
        System.out.println("Xeno's Reliquary loaded.");
    }
    
    @EventHandler
    public void onMessage(IMCEvent event) {
    	for(IMCMessage message: event.getMessages()) {
    		if(message.key.equals("DestructionCatalyst")) {
    			LogHelper.log(Level.INFO, "[Reliquary] Added block " + message.getStringValue() + " from " + message.getSender() + " was added to the Destruction Catalyst's registry.");
    			ItemDestructionCatalyst.ids.add(Integer.valueOf(message.getStringValue()));
    		}
    	}
    }

    public static void customBusterExplosion(Entity par1Entity,
            EntityPlayer player, double par2, double par4, double par6,
            float par8, boolean par9, boolean par10) {
        if (par1Entity.worldObj.isRemote)
            return;
        par1Entity.worldObj.newExplosion(par1Entity, par2, par4, par6, par8,
                par9, par10);
    }

    public static ConcussiveExplosion customConcussiveExplosion(
            Entity par1Entity, EntityPlayer player, double par2, double par4,
            double par6, float par8, boolean par9, boolean par10) {
        ConcussiveExplosion var11 = new ConcussiveExplosion(
                par1Entity.worldObj, par1Entity, player, par2, par4, par6, par8);
        var11.isFlaming = par9;
        var11.isSmoking = par10;
        var11.doExplosionA();
        var11.doExplosionB(false);

        return var11;
    }
}