package xreliquary.handler;


import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.handler.config.*;
import xreliquary.reference.Reference;

import java.io.File;
import java.util.*;


public class ConfigurationHandler
{

	public static Configuration configuration;

	public static void init(File configFile)
	{
		if (configuration == null)
		{
			configuration = new Configuration(configFile, true);
			loadConfiguration();
		}
	}

	private static void loadConfiguration()
	{
		HudConfiguration.loadHudPositions();
		EasyModeConfiguration.loadEasyModeSettings();
		MobDropConfiguration.loadMobDropProbabilities();
		BlockItemConfiguration.loadBlockAndItemSettings();
	}

	public static void postInit() {

		BlockItemConfiguration.loadEntitiesSettings();

		//loading here to allow for recipes with items from other mods
		// probably could be done earlier, but what do I know about when mods are loading stuff
		AlkahestConfiguration.loadAlkahestCraftingRecipes();
		AlkahestConfiguration.loadAlkahestChargingRecipes();
		AlkahestConfiguration.loadAlkahestBaseItem();

		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}

	public static List<String> getStringList(String name, String category, List<String> defaultValue) {
		return Arrays.asList(configuration.getStringList(name, category, defaultValue.toArray(new String[defaultValue.size()]), getTranslatedComment(category, name), new String[]{}, getLabelLangRef(category, name)));
	}

	public static boolean getBoolean(String name, String category, boolean defaultValue) {
		return configuration.getBoolean(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef( category, name));
	}

	public static int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
		return configuration.getInt(name, category, defaultValue, minValue, maxValue, getTranslatedComment(category, name) , getLabelLangRef(category, name));
	}

	public static String getString(String name, String category, String defaultValue) {
		return configuration.getString(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef(category, name));
	}

	public static String getTranslatedComment(String category, String config) {
		return StatCollector.translateToLocal("config." + category + "." + config + ".comment");
	}

	public static String getLabelLangRef(String category, String config) {
		return "config." + category + "." + config + ".label";
	}

	public static void setCategoryTranslations(String categoryName, boolean setComment) {
		ConfigCategory category = configuration.getCategory(categoryName);

		category.setLanguageKey("config." + categoryName + ".label");
		if (setComment) {
			category.setComment( StatCollector.translateToLocal("config." + categoryName + ".comment"));
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase( Reference.MOD_ID))
		{
			loadConfiguration();
			postInit();
		}
	}
}
