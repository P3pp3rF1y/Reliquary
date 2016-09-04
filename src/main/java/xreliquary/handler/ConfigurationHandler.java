package xreliquary.handler;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.handler.config.*;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

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
		BlockItemConfiguration.loadBlockAndItemSettings();

		Settings.chestLootEnabled = getBoolean(Names.chest_loot_enabled, "general", true, "Determines whether Reliquary items will be generated in chest loot (mostly mob drops, very rarely some lower level items)");
		configuration.getCategory("general").get(Names.chest_loot_enabled).setRequiresMcRestart(true);
		Settings.wailaShiftForInfo = getBoolean(Names.waila_shift_for_info, "general", false, "Whether player has to sneak to see additional info in waila");
		Settings.dropCraftingRecipesEnabled = getBoolean(Names.mob_drop_crafting_recipes_enabled, "general", false, "Determines wheter Reliquary mob drops have crafting recipes");
		configuration.getCategory("general").get(Names.mob_drop_crafting_recipes_enabled).setRequiresMcRestart(true);
		Settings.mobDropsEnabled = getBoolean(Names.mob_drops_enabled, "general", true, "Whether mobs drop the Reliquary mob drops. This won't remove mob drop items from registry and replace them with something else, but allows to turn off the additional drops when mobs are killed by player. If this is turned off the mob drop crafting recipes turned on by the other setting can be used.");
		configuration.getCategory("general").get(Names.mob_drops_enabled).setRequiresMcRestart(true);
		Settings.disabledItemsBlocks = ConfigurationHandler.getStringList(Names.disabled_items_blocks, "general", Collections.emptyList(), "List of items and blocks that are supposed to be disabled. By default this is empty, but you can use the names of the blocks and items (e.g. \"fertile_lilypad\", \"wraith_node\", \"glacial_staff\") in this list and mod will not register those. It will also not register any recipes that include whatever is disabled.");
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

	public static List<String> getStringList(String name, String category, List<String> defaultValue, String comment) {
		return Arrays.asList(configuration.getStringList(name, category, defaultValue.toArray(new String[defaultValue.size()]), comment, new String[] {}, getConfigLangRef(category, name)));
	}

	public static boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
		return configuration.getBoolean(name, category, defaultValue, comment, getConfigLangRef(category, name));
	}

	public static int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
		return configuration.getInt(name, category, defaultValue, minValue, maxValue, comment, getConfigLangRef(category, name));
	}

	public static String getString(@SuppressWarnings("SameParameterValue") String name, String category, String defaultValue, String comment) {
		return configuration.getString(name, category, defaultValue, comment, getConfigLangRef(category, name));
	}

	private static String getConfigLangRef(String category, String config) {
		return "xreliquary.config." + category + "." + config + ".label";
	}

	public static String getCategoryLangRef(String category) {
		return "xreliquary.config." + category + ".label";
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
			postInit();
		}
	}
}
