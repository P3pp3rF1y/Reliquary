package xreliquary.handler.config;

import net.minecraftforge.common.config.ConfigCategory;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class EasyModeConfiguration {
	@SuppressWarnings("ConstantConditions")
	public static void loadEasyModeSettings() {
		boolean easyModeDefault = true;

		Settings.EasyModeRecipes.fortuneCoin = ConfigurationHandler.getBoolean(Names.Items.FORTUNE_COIN, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Fortune Coin");
		Settings.EasyModeRecipes.altar = ConfigurationHandler.getBoolean(Names.Blocks.ALTAR, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Altar of Light");
		Settings.EasyModeRecipes.infernalChalice = ConfigurationHandler.getBoolean(Names.Items.INFERNAL_CHALICE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Infernal Chalice");
		Settings.EasyModeRecipes.enderStaff = ConfigurationHandler.getBoolean(Names.Items.ENDER_STAFF, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Ender Staff");
		Settings.EasyModeRecipes.salamanderEye = ConfigurationHandler.getBoolean(Names.Items.SALAMANDER_EYE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Salamander Eye");
		Settings.EasyModeRecipes.rodOfLyssa = ConfigurationHandler.getBoolean(Names.Items.ROD_OF_LYSSA, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Rod of Lyssa");
		Settings.EasyModeRecipes.serpentStaff = ConfigurationHandler.getBoolean(Names.Items.SERPENT_STAFF, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Serpent Staff");
		Settings.EasyModeRecipes.rendingGale = ConfigurationHandler.getBoolean(Names.Items.RENDING_GALE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Rending Gale");
		Settings.EasyModeRecipes.pyromancerStaff = ConfigurationHandler.getBoolean(Names.Items.PYROMANCER_STAFF, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Pyromancer Staff");
		Settings.EasyModeRecipes.magicBane = ConfigurationHandler.getBoolean(Names.Items.MAGICBANE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Magicbane");
		Settings.EasyModeRecipes.lanternOfParanoia = ConfigurationHandler.getBoolean(Names.Items.LANTERN_OF_PARANOIA, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Lantern of Paranoia");
		Settings.EasyModeRecipes.alkahestryTome = ConfigurationHandler.getBoolean(Names.Items.ALKAHESTRY_TOME, Names.Configs.EASY_MODE_RECIPES, false, "Easy mode recipe for Alkahestry Tome");
		Settings.EasyModeRecipes.wraithNode = ConfigurationHandler.getBoolean(Names.Blocks.WRAITH_NODE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Wraith Node");
		Settings.EasyModeRecipes.glacialStaff = ConfigurationHandler.getBoolean(Names.Items.GLACIAL_STAFF, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Glacial Staff");
		Settings.EasyModeRecipes.sojournerStaff = ConfigurationHandler.getBoolean(Names.Items.SOJOURNER_STAFF, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Sojourner Staff");
		Settings.EasyModeRecipes.krakenShell = ConfigurationHandler.getBoolean(Names.Items.KRAKEN_SHELL, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Kraken Shell");
		Settings.EasyModeRecipes.angelicFeather = ConfigurationHandler.getBoolean(Names.Items.ANGELIC_FEATHER, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Angelic Feather");
		Settings.EasyModeRecipes.emperorChalice = ConfigurationHandler.getBoolean(Names.Items.EMPEROR_CHALICE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Emperor Chalice");
		Settings.EasyModeRecipes.heroMedallion = ConfigurationHandler.getBoolean(Names.Items.HERO_MEDALLION, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Hero Medallion");
		Settings.EasyModeRecipes.iceMagusRod = ConfigurationHandler.getBoolean(Names.Items.ICE_MAGUS_ROD, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Ice Magus Rod");
		Settings.EasyModeRecipes.infernalClaws = ConfigurationHandler.getBoolean(Names.Items.INFERNAL_CLAWS, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Infernal Claws");
		Settings.EasyModeRecipes.destructionCatalyst = ConfigurationHandler.getBoolean(Names.Items.DESTRUCTION_CATALYST, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Destruction Catalyst");
		Settings.EasyModeRecipes.interdictionTorch = ConfigurationHandler.getBoolean(Names.Blocks.INTERDICTION_TORCH, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Interdiction Torch");
		Settings.EasyModeRecipes.voidTear = ConfigurationHandler.getBoolean(Names.Items.VOID_TEAR, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Void Tear");
		Settings.EasyModeRecipes.infernalTear = ConfigurationHandler.getBoolean(Names.Items.INFERNAL_TEAR, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Infernal Tear");
		Settings.EasyModeRecipes.fertileEssence = ConfigurationHandler.getBoolean(Names.Items.FERTILE_ESSENCE, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Fertile Essence");
		Settings.EasyModeRecipes.seekerShot = ConfigurationHandler.getBoolean(Names.Items.SEEKER_SHOT, Names.Configs.EASY_MODE_RECIPES, easyModeDefault, "Easy mode recipe for Seeker Shot");

		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.Configs.EASY_MODE_RECIPES);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(Names.Configs.EASY_MODE_RECIPES));
		category.setComment("Settings for easy mode vs hard mode recipes for items and blocks");
	}
}
