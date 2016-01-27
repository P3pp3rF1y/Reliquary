package xreliquary;

import lib.enderwizards.sandstone.Sandstone;
import lib.enderwizards.sandstone.init.Content;
import lib.enderwizards.sandstone.mod.SandstoneMod;
import lib.enderwizards.sandstone.mod.config.Config;
import lib.enderwizards.sandstone.util.WorldDataHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xreliquary.common.CommonProxy;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.init.ModBlocks;
import xreliquary.init.ModItems;
import xreliquary.integration.NEIModIntegration;
import xreliquary.reference.Reference;
import xreliquary.network.PacketHandler;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import java.io.File;

//@ModstatInfo(prefix = "reliquary")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:libsandstone", guiFactory = "xreliquary.client.gui.XRGuiFactory")
@SandstoneMod(basePackage = "xreliquary")
public class Reliquary {

    @Instance(Reference.MOD_ID)
    public static Reliquary INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy PROXY;

    public static Content CONTENT;
    public static CreativeTabs CREATIVE_TAB = new CreativeTabXR(CreativeTabs.getNextID(), Reference.MOD_ID);
    public static Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init( event.getSuggestedConfigurationFile() );

        CONTENT = Sandstone.preInit();

        ModBlocks.init();

        ModItems.init();

        WorldDataHandler.register();

        //TODO: add colors when removing libSandstone
/*        *//* Unicode colors that you can use in the tooltips/names lang files.
         * Use by calling {{!name}}, with name being the name being colors.color. *//*
        LanguageHelper.globals.put("colors.black", "\u00A70");
        LanguageHelper.globals.put("colors.navy", "\u00A71");
        LanguageHelper.globals.put("colors.green", "\u00A72");
        LanguageHelper.globals.put("colors.blue", "\u00A73");
        LanguageHelper.globals.put("colors.red", "\u00A74");
        LanguageHelper.globals.put("colors.purple", "\u00A75");
        LanguageHelper.globals.put("colors.gold", "\u00A76");
        LanguageHelper.globals.put("colors.light_gray", "\u00A77");
        LanguageHelper.globals.put("colors.gray", "\u00A78");
        LanguageHelper.globals.put("colors.dark_purple", "\u00A79");
        LanguageHelper.globals.put("colors.light_green", "\u00A7a");
        LanguageHelper.globals.put("colors.light_blue", "\u00A7b");
        LanguageHelper.globals.put("colors.rose", "\u00A7c");
        LanguageHelper.globals.put("colors.light_purple", "\u00A7d");
        LanguageHelper.globals.put("colors.yellow", "\u00A7e");
        LanguageHelper.globals.put("colors.white", "\u00A7f");
        LanguageHelper.globals.put("colors.reset", EnumChatFormatting.RESET.toString());*/
        //important that this initializes AFTER items already exist.
        //TODO: add back when testing potions
        //PotionMap.init();

        //important that this initializes before the pre-init phase
        //PROXY.initRecipeDisablers();

        PROXY.preInit();
        PacketHandler.init();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        //TODO: put modstats back in when ready
        //Modstats.instance().getReporter().registerMod(this);
        Sandstone.addModIntegration(new NEIModIntegration());

        PROXY.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit();
        ConfigurationHandler.postInit();

        //and finally save the file changes. post init is the last stage of configuration, it does an entity scan, hopefully it's cross-mod compatible.
        //CONFIG.save();

        //finally, initialize the potion list, this is done after ensuring configs.
        //TODO: add back when testing potions
        //PotionMap.initializePotionMappings();

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
                    LOGGER.log(Level.INFO, "[IMC] Added AlkahestRecipe ID: " + Item.itemRegistry.getNameForObject(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")).getItem()) + " from " + message.getSender() + " to registry.");
                } else {
                    LOGGER.log(Level.WARN, "[IMC] Invalid AlkahestRecipe from " + message.getSender() + "! Please contact the mod author if you see this error occurring.");
                }
            }
        }
    }

}