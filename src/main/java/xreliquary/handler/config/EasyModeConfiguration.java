package xreliquary.handler.config;


import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;


public class EasyModeConfiguration
{
	public static void loadEasyModeSettings()
	{
		boolean easyModeDefault = true;

		Settings.EasyModeRecipes.fortuneCoin = ConfigurationHandler.getBoolean( Names.fortune_coin, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.altar = ConfigurationHandler.getBoolean(Names.altar, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalChalice = ConfigurationHandler.getBoolean(Names.infernal_chalice, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.enderStaff = ConfigurationHandler.getBoolean(Names.ender_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.salamanderEye = ConfigurationHandler.getBoolean(Names.salamander_eye, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.rodOfLyssa = ConfigurationHandler.getBoolean(Names.rod_of_lyssa, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.serpentStaff = ConfigurationHandler.getBoolean(Names.serpent_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.rendingGale = ConfigurationHandler.getBoolean(Names.rending_gale, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.pyromancerStaff = ConfigurationHandler.getBoolean(Names.pyromancer_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.magicBane = ConfigurationHandler.getBoolean(Names.magicbane, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.lanternOfParanoia = ConfigurationHandler.getBoolean(Names.lantern_of_paranoia, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.alkahestryTome = ConfigurationHandler.getBoolean(Names.alkahestry_tome, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.wraithNode = ConfigurationHandler.getBoolean(Names.wraith_node, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.glacialStaff = ConfigurationHandler.getBoolean(Names.glacial_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.sojournerStaff = ConfigurationHandler.getBoolean(Names.sojourner_staff, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.krakenShell = ConfigurationHandler.getBoolean(Names.kraken_shell, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.angelicFeather = ConfigurationHandler.getBoolean(Names.angelic_feather, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.emperorChalice = ConfigurationHandler.getBoolean(Names.emperor_chalice, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.heroMedallion = ConfigurationHandler.getBoolean(Names.hero_medallion, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.iceMagusRod = ConfigurationHandler.getBoolean(Names.ice_magus_rod, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalClaws = ConfigurationHandler.getBoolean(Names.infernal_claws, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.destructionCatalyst = ConfigurationHandler.getBoolean(Names.destruction_catalyst, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.interdictionTorch = ConfigurationHandler.getBoolean(Names.interdiction_torch, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.voidTear = ConfigurationHandler.getBoolean(Names.void_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.infernalTear = ConfigurationHandler.getBoolean(Names.infernal_tear, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.fertileEssence = ConfigurationHandler.getBoolean(Names.fertile_essence, Names.easy_mode_recipes, easyModeDefault);
		Settings.EasyModeRecipes.seekerShot = ConfigurationHandler.getBoolean(Names.seeker_shot, Names.easy_mode_recipes, easyModeDefault);

		ConfigurationHandler.setCategoryTranslations(Names.easy_mode_recipes, true);
	}
}
