package xreliquary;

import lib.enderwizards.sandstone.Sandstone;
import lib.enderwizards.sandstone.init.Content;
import lib.enderwizards.sandstone.mod.SandstoneMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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
import xreliquary.reference.Reference;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Settings;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;

//@ModstatInfo(prefix = "reliquary")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
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

        PROXY.initColors();

        CONTENT = Sandstone.preInit();

        ModBlocks.init();

        ModItems.init();

        PROXY.preInit();

        //important that this initializes before the pre-init phase
        //PROXY.initRecipeDisablers();


        //TODO figure out a better way to handle this if possible
        ConfigurationHandler.loadPotionMap();

        PacketHandler.init();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        //TODO: put modstats back in when ready
        //Modstats.instance().getReporter().registerMod(this);

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

    }

    @EventHandler
    public void onMessage(IMCEvent event) {
        for (IMCMessage message : event.getMessages()) {
            if (message.key.equals("Alkahest")) {
                NBTTagCompound tag = message.getNBTValue();
                if (tag != null && ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")) != null && tag.hasKey("yield") && tag.hasKey("cost")) {
                    if (tag.hasKey("dictionaryName"))
                        Settings.AlkahestryTome.craftingRecipes.put("OreDictionary:" + tag.getString("dictionaryName"),new AlkahestCraftRecipe(tag.getString("dictionaryName"), tag.getInteger("yield"), tag.getInteger("cost")));
                    else
                        Settings.AlkahestryTome.craftingRecipes.put(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")).getItem().getRegistryName(), new AlkahestCraftRecipe(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")), tag.getInteger("yield"), tag.getInteger("cost")));
                    LOGGER.log(Level.INFO, "[IMC] Added AlkahestRecipe ID: " + Item.itemRegistry.getNameForObject(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")).getItem()) + " from " + message.getSender() + " to registry.");
                } else {
                    LOGGER.log(Level.WARN, "[IMC] Invalid AlkahestRecipe from " + message.getSender() + "! Please contact the mod author if you see this error occurring.");
                }
            }
        }
    }

}