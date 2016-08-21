package xreliquary.handler;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.handler.config.*;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;
import xreliquary.util.LanguageHelper;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigurationHandler {

	public static Configuration configuration;

	public static void init(File configFile) {
		if(configuration == null) {
			configuration = new Configuration(configFile, true);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		HudConfiguration.loadHudPositions();
		EasyModeConfiguration.loadEasyModeSettings();
		MobDropConfiguration.loadMobDropProbabilities();
		BlockItemConfiguration.loadBlockAndItemSettings();

		Settings.chestLootEnabled = getBoolean(Names.chest_loot_enabled, "general", true);
		configuration.getCategory("general").get(Names.chest_loot_enabled).setRequiresMcRestart(true);
		Settings.wailaShiftForInfo = getBoolean(Names.waila_shift_for_info, "general", false);
		Settings.dropCraftingRecipesEnabled = getBoolean(Names.mob_drop_crafting_recipes_enabled, "general", false);
		configuration.getCategory("general").get(Names.mob_drop_crafting_recipes_enabled).setRequiresMcRestart(true);
		Settings.mobDropsEnabled = getBoolean(Names.mob_drops_enabled, "general", true);
		configuration.getCategory("general").get(Names.mob_drops_enabled).setRequiresMcRestart(true);
		Settings.disabledItemsBlocks = ConfigurationHandler.getStringList(Names.disabled_items_blocks, "general", Collections.EMPTY_LIST);
		configuration.getCategory("general").get(Names.disabled_items_blocks).setRequiresMcRestart(true);
	}

	public static void postInit() {

		BlockItemConfiguration.loadEntitiesSettings();

		//loading here to allow for recipes with items from other mods
		// probably could be done earlier, but what do I know about when mods are loading stuff
		AlkahestConfiguration.loadAlkahestCraftingRecipes();
		AlkahestConfiguration.loadAlkahestChargingRecipes();
		AlkahestConfiguration.loadAlkahestBaseItem();

		if(configuration.hasChanged()) {
			configuration.save();
		}
	}

	public static List<String> getStringList(String name, String category, List<String> defaultValue) {
		return Arrays.asList(configuration.getStringList(name, category, defaultValue.toArray(new String[defaultValue.size()]), getTranslatedComment(category, name), new String[] {}, getLabelLangRef(category, name)));
	}

	public static boolean getBoolean(String name, String category, boolean defaultValue) {
		return configuration.getBoolean(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef(category, name));
	}

	public static int getInt(String name, String category, int defaultValue, int minValue, int maxValue) {
		return configuration.getInt(name, category, defaultValue, minValue, maxValue, getTranslatedComment(category, name), getLabelLangRef(category, name));
	}

	public static String getString(String name, String category, String defaultValue) {
		return configuration.getString(name, category, defaultValue, getTranslatedComment(category, name), getLabelLangRef(category, name));
	}

	public static String getTranslatedComment(String category, String config) {
		return LanguageHelper.getLocalization("config." + category + "." + config + ".comment");
	}

	public static String getLabelLangRef(String category, String config) {
		return "config." + category + "." + config + ".label";
	}

	public static void setCategoryTranslations(String categoryName, boolean setComment) {
		ConfigCategory category = configuration.getCategory(categoryName);

		category.setLanguageKey("config." + categoryName + ".label");
		if(setComment) {
			category.setComment(LanguageHelper.getLocalization("config." + categoryName + ".comment"));
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
			postInit();
		}
	}
}
