package xreliquary;

import java.util.logging.Level;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import xreliquary.items.ItemDestructionCatalyst;
import xreliquary.items.alkahestry.Alkahestry;
import xreliquary.lib.Reference;
import xreliquary.proxy.CommonProxy;
import xreliquary.util.AlkahestRecipe;
import xreliquary.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

@ModstatInfo(prefix = "reliquary")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class Reliquary {

	@Instance(Reference.MOD_ID)
	public static Reliquary INSTANCE;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy PROXY;

	public static Configuration CONFIG;
	public static CreativeTabs CREATIVE_TAB = new CreativeTabXR(CreativeTabs.getNextID(), Reference.MOD_ID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		LogHelper.init();
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());
		CONFIG.load();

		PROXY.preInit();

		CONFIG.save();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		Modstats.instance().getReporter().registerMod(this);

		PROXY.init();
		MinecraftForge.EVENT_BUS.register(this);

		LanguageRegistry.instance().loadLocalization("/assets/xreliquary/lang/en_US.lang", "en_US", false);
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event) {
		LogHelper.log(Level.INFO, "Loaded successfully!");
	}

	@EventHandler
	public void onMessage(IMCEvent event) {
		for (IMCMessage message : event.getMessages()) {
			if (message.key.equals("DestructionCatalyst")) {
				LogHelper.log(Level.INFO, "[IMC] Added block " + message.getStringValue() + " from " + message.getSender() + " was added to the Destruction Catalyst's registry.");
				ItemDestructionCatalyst.ids.add(Integer.valueOf(message.getStringValue()));
			} else if (message.key.equals("Alkahest")) {
				NBTTagCompound tag = message.getNBTValue();
				if (tag != null && ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")) != null && tag.hasKey("yield") && tag.hasKey("cost")) {
					if (tag.hasKey("dictionaryName"))
						Alkahestry.addKey(new AlkahestRecipe(tag.getString("dictionaryName"), tag.getInteger("yield"), tag.getInteger("cost")));
					else
						Alkahestry.addKey(new AlkahestRecipe(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")), tag.getInteger("yield"), tag.getInteger("cost")));
					LogHelper.log(Level.INFO, "[IMC] Added AlkahestRecipe ID: " + String.valueOf(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item")).itemID) + " from " + message.getSender() + " to registry.");
				} else {
					LogHelper.log(Level.WARNING, "[IMC] Invalid AlkahestRecipe from " + message.getSender() + "! Please contact the mod author if you see this error occuring.");
				}
			}
		}
	}

}