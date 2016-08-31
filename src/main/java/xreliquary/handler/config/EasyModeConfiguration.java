package xreliquary.handler.config;

import net.minecraftforge.common.config.ConfigCategory;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class EasyModeConfiguration {
	@SuppressWarnings("ConstantConditions")
	public static void loadEasyModeSettings() {
		boolean easyModeDefault = true;

		Settings.EasyModeRecipes.fortuneCoin = ConfigurationHandler.getBoolean(Names.fortune_coin, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Fortune Coin");
		Settings.EasyModeRecipes.altar = ConfigurationHandler.getBoolean(Names.altar, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Altar of Light");
		Settings.EasyModeRecipes.infernalChalice = ConfigurationHandler.getBoolean(Names.infernal_chalice, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Infernal Chalice");
		Settings.EasyModeRecipes.enderStaff = ConfigurationHandler.getBoolean(Names.ender_staff, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Ender Staff");
		Settings.EasyModeRecipes.salamanderEye = ConfigurationHandler.getBoolean(Names.salamander_eye, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Salamander Eye");
		Settings.EasyModeRecipes.rodOfLyssa = ConfigurationHandler.getBoolean(Names.rod_of_lyssa, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Rod of Lyssa");
		Settings.EasyModeRecipes.serpentStaff = ConfigurationHandler.getBoolean(Names.serpent_staff, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Serpent Staff");
		Settings.EasyModeRecipes.rendingGale = ConfigurationHandler.getBoolean(Names.rending_gale, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Rending Gale");
		Settings.EasyModeRecipes.pyromancerStaff = ConfigurationHandler.getBoolean(Names.pyromancer_staff, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Pyromancer Staff");
		Settings.EasyModeRecipes.magicBane = ConfigurationHandler.getBoolean(Names.magicbane, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Magicbane");
		Settings.EasyModeRecipes.lanternOfParanoia = ConfigurationHandler.getBoolean(Names.lantern_of_paranoia, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Lantern of Paranoia");
		Settings.EasyModeRecipes.alkahestryTome = ConfigurationHandler.getBoolean(Names.alkahestry_tome, Names.easy_mode_recipes, false, "Easy mode recipe for Alkahestry Tome");
		Settings.EasyModeRecipes.wraithNode = ConfigurationHandler.getBoolean(Names.wraith_node, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Wraith Node");
		Settings.EasyModeRecipes.glacialStaff = ConfigurationHandler.getBoolean(Names.glacial_staff, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Glacial Staff");
		Settings.EasyModeRecipes.sojournerStaff = ConfigurationHandler.getBoolean(Names.sojourner_staff, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Sojourner Staff");
		Settings.EasyModeRecipes.krakenShell = ConfigurationHandler.getBoolean(Names.kraken_shell, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Kraken Shell");
		Settings.EasyModeRecipes.angelicFeather = ConfigurationHandler.getBoolean(Names.angelic_feather, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Angelic Feather");
		Settings.EasyModeRecipes.emperorChalice = ConfigurationHandler.getBoolean(Names.emperor_chalice, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Emperor Chalice");
		Settings.EasyModeRecipes.heroMedallion = ConfigurationHandler.getBoolean(Names.hero_medallion, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Hero Medallion");
		Settings.EasyModeRecipes.iceMagusRod = ConfigurationHandler.getBoolean(Names.ice_magus_rod, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Ice Magus Rod");
		Settings.EasyModeRecipes.infernalClaws = ConfigurationHandler.getBoolean(Names.infernal_claws, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Infernal Claws");
		Settings.EasyModeRecipes.destructionCatalyst = ConfigurationHandler.getBoolean(Names.destruction_catalyst, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Destruction Catalyst");
		Settings.EasyModeRecipes.interdictionTorch = ConfigurationHandler.getBoolean(Names.interdiction_torch, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Interdiction Torch");
		Settings.EasyModeRecipes.voidTear = ConfigurationHandler.getBoolean(Names.void_tear, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Void Tear");
		Settings.EasyModeRecipes.infernalTear = ConfigurationHandler.getBoolean(Names.infernal_tear, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Infernal Tear");
		Settings.EasyModeRecipes.fertileEssence = ConfigurationHandler.getBoolean(Names.fertile_essence, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Fertile Essence");
		Settings.EasyModeRecipes.seekerShot = ConfigurationHandler.getBoolean(Names.seeker_shot, Names.easy_mode_recipes, easyModeDefault, "Easy mode recipe for Seeker Shot");

		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.easy_mode_recipes);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(Names.easy_mode_recipes));
		category.setComment("Settings for easy mode vs hard mode recipes for items and blocks");
	}
}
