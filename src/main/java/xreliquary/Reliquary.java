package xreliquary;

import java.util.logging.Level;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import com.google.common.collect.ImmutableList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import xreliquary.blocks.XRBlocks;
import xreliquary.common.CommonProxy;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.items.XRItems;
import xreliquary.items.alkahestry.AlkahestryRegistry;
import xreliquary.lib.Reference;
import xreliquary.util.AlkahestRecipe;
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

@ModstatInfo(prefix="reliquary")
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
        AlkahestryRegistry.init();

        FluidContainerRegistry.registerFluidContainer(new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME / 8), new ItemStack(XRItems.condensedPotion), XRItems.potion(Reference.WATER_META));
        
        XRBlocks.init();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
    	Modstats.instance().getReporter().registerMod(this);
    	
        proxy.registerEntityTrackers();
        proxy.registerRenderers();
        MinecraftForge.EVENT_BUS.register(this);
        
        proxy.registerTileEntities();
        LanguageRegistry.instance().addStringLocalization("itemGroup." + Reference.MOD_ID, Reference.MOD_NAME);
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent event) {
        System.out.println(Reference.MOD_NAME + " loaded.");
    }
    
    @EventHandler
    public void onMessage(IMCEvent event) {
    	for(IMCMessage message: event.getMessages()) {
    		if(message.key.equals("DestructionCatalyst")) {
    			LogHelper.log(Level.INFO, "[IMC] Added block " + message.getStringValue() + " from " + message.getSender() + " was added to the Destruction Catalyst's registry.");
    			ItemDestructionCatalyst.ids.add(Integer.valueOf(message.getStringValue()));
    		} else if(message.key.equals("Alkahest")) {
    			NBTTagCompound tag = message.getNBTValue();
    			if(tag != null && ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")) != null && tag.hasKey("yield") && tag.hasKey("cost")) {
    				AlkahestryRegistry.addKey(new AlkahestRecipe(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")), tag.getInteger("yield"), tag.getInteger("cost")));
    				LogHelper.log(Level.INFO, "[IMC] Added AlkahestRecipe ID: " + String.valueOf(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")).itemID) + " from " + message.getSender() + " to registry.");
    			} else {
    				LogHelper.log(Level.WARNING, "[IMC] Invalid AlkahestRecipe from " + message.getSender() + "! Please contact the mod author if you see this error occuring.");
    			}
    		}
    	}
    }

}