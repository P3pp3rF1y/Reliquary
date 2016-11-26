package xreliquary;

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
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import xreliquary.common.CommonProxy;
import xreliquary.compat.ICompat;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.handler.config.PotionConfiguration;
import xreliquary.init.*;
import xreliquary.network.PacketHandler;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LogHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.pedestal.PedestalRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS, dependencies = Reference.DEPENDENCIES)
public class Reliquary {

	@Instance(Reference.MOD_ID)
	public static Reliquary INSTANCE;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy PROXY;

	public static CreativeTabs CREATIVE_TAB;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());

 		CREATIVE_TAB = new CreativeTabXR(CreativeTabs.getNextID());

		PROXY.initColors();

		ModBlocks.init();

		ModItems.init();

		ModFluids.preInit();

		ModLoot.init();

		ModPotions.init();

		ModCapabilities.init();

		ModSounds.init();

		PROXY.preInit();

		//TODO figure out a better way to handle this if possible
		PotionConfiguration.loadPotionMap();

		PROXY.initSpecialJEIDescriptions();

		PacketHandler.init();

		ModCompat.registerModCompat();
		ModCompat.loadCompat(ICompat.InitializationPhase.PRE_INIT, null);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		ModFluids.init();

		PROXY.init();

		MinecraftForge.EVENT_BUS.register(this);

		ModCompat.loadCompat(ICompat.InitializationPhase.INIT, null);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		PROXY.postInit();

		ConfigurationHandler.postInit();

		ModCompat.loadCompat(ICompat.InitializationPhase.POST_INIT, null);

		PedestalItems.init();

		LogHelper.info("Loaded successfully!");
	}

	@EventHandler
	public void onMessage(IMCEvent event) {
		event.getMessages().stream().filter(message -> message.key.equals("Alkahest")).forEach(message -> {
			NBTTagCompound tag = message.getNBTValue();
			if(tag != null && !new ItemStack(tag.getCompoundTag("item")).isEmpty() && tag.hasKey("yield") && tag.hasKey("cost")) {
				if(tag.hasKey("dictionaryName"))
					Settings.AlkahestryTome.craftingRecipes.put("OreDictionary:" + tag.getString("dictionaryName"), new AlkahestCraftRecipe(tag.getString("dictionaryName"), tag.getInteger("yield"), tag.getInteger("cost")));
				else
					Settings.AlkahestryTome.craftingRecipes.put(new ItemStack(tag.getCompoundTag("item")).getItem().getRegistryName().toString(), new AlkahestCraftRecipe(new ItemStack(tag.getCompoundTag("item")), tag.getInteger("yield"), tag.getInteger("cost")));
				LogHelper.info("[IMC] Added AlkahestRecipe ID: " + Item.REGISTRY.getNameForObject(new ItemStack(tag.getCompoundTag("item")).getItem()) + " from " + message.getSender() + " to registry.");
			} else {
				LogHelper.warn("[IMC] Invalid AlkahestRecipe from " + message.getSender() + "! Please contact the mod author if you see this error occurring.");
			}
		});
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		PedestalRegistry.clearPositions();
	}
}
