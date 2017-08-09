package xreliquary.handler;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xreliquary.handler.config.BlockItemConfiguration;
import xreliquary.handler.config.HudConfiguration;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.reference.Settings;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
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
		BlockItemConfiguration.loadBlockAndItemSettings();

		Settings.chestLootEnabled = getBoolean(Names.Configs.CHEST_LOOT_ENABLED, "general", true, "Determines whether Reliquary items will be generated in chest loot (mostly mob drops, very rarely some lower level items)");
		configuration.getCategory("general").get(Names.Configs.CHEST_LOOT_ENABLED).setRequiresMcRestart(true);
		Settings.wailaShiftForInfo = getBoolean(Names.Configs.WAILA_SHIFT_FOR_INFO, "general", false, "Whether player has to sneak to see additional info in waila");
		Settings.dropCraftingRecipesEnabled = getBoolean(Names.Configs.MOB_DROP_CRAFTING_RECIPES_ENABLED, "general", false, "Determines wheter Reliquary mob drops have crafting recipes");
		configuration.getCategory("general").get(Names.Configs.MOB_DROP_CRAFTING_RECIPES_ENABLED).setRequiresMcRestart(true);
		Settings.mobDropsEnabled = getBoolean(Names.Configs.MOB_DROPS_ENABLED, "general", true, "Whether mobs drop the Reliquary mob drops. This won't remove mob drop items from registry and replace them with something else, but allows to turn off the additional drops when mobs are killed by player. If this is turned off the mob drop crafting recipes turned on by the other setting can be used.");
		configuration.getCategory("general").get(Names.Configs.MOB_DROPS_ENABLED).setRequiresMcRestart(true);
	}

	public static void postInit() {

		BlockItemConfiguration.loadEntitiesSettings();

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
	public static void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
			postInit();
		}
	}
}
