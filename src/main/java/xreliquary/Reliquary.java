package xreliquary;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.enderwizards.sandstone.Sandstone;
import lib.enderwizards.sandstone.init.Content;
import lib.enderwizards.sandstone.mod.SandstoneMod;
import lib.enderwizards.sandstone.mod.config.Config;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xreliquary.common.CommonProxy;
import xreliquary.integration.NEIModIntegration;
import xreliquary.lib.Reference;
import xreliquary.network.PacketHandler;
import lib.enderwizards.sandstone.util.WorldDataHandler;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;
import xreliquary.util.potions.PotionMap;

import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:libsandstone", guiFactory = "xreliquary.client.gui.XRGuiFactory")
@SandstoneMod(basePackage = "xreliquary")
public class Reliquary {

    @Instance(Reference.MOD_ID)
    public static Reliquary INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy PROXY;

    public static Content CONTENT;
    public static Config CONFIG;
    public static CreativeTabs CREATIVE_TAB = new CreativeTabXR(CreativeTabs.getNextID(), Reference.MOD_ID);
    public static Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CONFIG = Config.toml(new File(event.getModConfigurationDirectory(), Reference.MOD_ID + ".cfg"));
        PROXY.initOptions();

        CONTENT = Sandstone.preInit();

        WorldDataHandler.register();

        //important that this initializes AFTER items already exist.
        PotionMap.init();

        //important that this initializes before the pre-init phase
        PROXY.initRecipeDisablers();

        PROXY.preInit();
        PacketHandler.init();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Sandstone.addModIntegration(new NEIModIntegration());

        PROXY.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();

        //and finally save the file changes. post init is the last stage of configuration, it does an entity scan, hopefully it's cross-mod compatible.
        CONFIG.save();

        //finally, initialize the potion list, this is done after ensuring configs.
        PotionMap.initializePotionMappings();

        LOGGER.log(Level.INFO, "Loaded successfully!");
        Sandstone.postInit();

    }

    @EventHandler
    public void onMessage(IMCEvent event) {
        for (IMCMessage message : event.getMessages()) {
            if (message.key.equals("Alkahest")) {
                NBTTagCompound tag = message.getNBTValue();
                if (tag != null && ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")) != null && tag.hasKey("yield") && tag.hasKey("cost")) {
                    if (tag.hasKey("dictionaryName"))
                        Alkahestry.addKey(new AlkahestRecipe(tag.getString("dictionaryName"), tag.getInteger("yield"), tag.getInteger("cost")));
                    else
                        Alkahestry.addKey(new AlkahestRecipe(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")), tag.getInteger("yield"), tag.getInteger("cost")));
                    LOGGER.log(Level.INFO, "[IMC] Added AlkahestRecipe ID: " + Item.itemRegistry.getNameForObject(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"))) + " from " + message.getSender() + " to registry.");
                } else {
                    LOGGER.log(Level.WARN, "[IMC] Invalid AlkahestRecipe from " + message.getSender() + "! Please contact the mod author if you see this error occurring.");
                }
            }
        }
    }

}