package xreliquary;

import java.io.File;

import lib.enderwizards.sandstone.Sandstone;
import lib.enderwizards.sandstone.mod.SandstoneMod;
import lib.enderwizards.sandstone.mod.config.Configuration;
import lib.enderwizards.sandstone.mod.config.TomlConfig;
import net.minecraft.item.Item;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.util.alkahestry.Alkahestry;
import xreliquary.lib.Reference;
import xreliquary.common.CommonProxy;
import xreliquary.util.alkahestry.AlkahestRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@ModstatInfo(prefix = "reliquary")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:libsandstone")
@SandstoneMod(basePackage = "xreliquary")
public class Reliquary {

	@Instance(Reference.MOD_ID)
	public static Reliquary INSTANCE;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy PROXY;

	public static TomlConfig CONFIG;
	public static CreativeTabs CREATIVE_TAB = new CreativeTabXR(CreativeTabs.getNextID(), Reference.MOD_ID);
	public static Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CONFIG = Configuration.toml(new File(event.getModConfigurationDirectory(), Reference.MOD_ID + ".toml"));

		PROXY.initOptions();
		Sandstone.preInit();
		PROXY.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		Modstats.instance().getReporter().registerMod(this);

		PROXY.init();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LOGGER.log(Level.INFO, "Loaded successfully!");
		if (event.getSide() == Side.CLIENT && Loader.isModLoaded("NotEnoughItems")) {
			LOGGER.log(Level.INFO, "Hey NEI! I got a plugin for you! (hopefully in the near future).");
		}
	}

	@EventHandler
	public void onMessage(IMCEvent event) {
		for (IMCMessage message : event.getMessages()) {
			if (message.key.equals("DestructionCatalyst")) {
				LOGGER.log(Level.INFO, "[IMC] Added block " + message.getStringValue() + " from " + message.getSender() + " was added to the Destruction Catalyst's registry.");
				ItemDestructionCatalyst.ids.add(message.getStringValue());
			} else if (message.key.equals("Alkahest")) {
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