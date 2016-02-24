package xreliquary.handler.config;


import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;


public class HudConfiguration
{
	public static void loadHudPositions()
	{
		Settings.HudPositions.sojournerStaff = ConfigurationHandler.getInt( Names.sojourner_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.handgun = ConfigurationHandler.getInt(Names.handgun, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.alkahestryTome = ConfigurationHandler.getInt(Names.alkahestry_tome, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.destructionCatalyst = ConfigurationHandler.getInt(Names.destruction_catalyst, Names.hud_positions, 3, 0, 3);
		//Settings.HudPositions.elsewhereFlask = getInt(Names.elsewhere_flask, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.enderStaff = ConfigurationHandler.getInt(Names.ender_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.iceMagusRod = ConfigurationHandler.getInt(Names.ice_magus_rod, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.glacialStaff = ConfigurationHandler.getInt(Names.glacial_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.voidTear = ConfigurationHandler.getInt(Names.void_tear, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.midasTouchstone = ConfigurationHandler.getInt(Names.midas_touchstone, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.harvestRod = ConfigurationHandler.getInt(Names.harvest_rod, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.infernalChalice = ConfigurationHandler.getInt(Names.infernal_chalice, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.heroMedallion = ConfigurationHandler.getInt(Names.hero_medallion, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.pyromancerStaff = ConfigurationHandler.getInt(Names.pyromancer_staff, Names.hud_positions, 3, 0, 3);
		Settings.HudPositions.rendingGale = ConfigurationHandler.getInt(Names.rending_gale, Names.hud_positions, 3, 0, 3);

		ConfigurationHandler.setCategoryTranslations(Names.hud_positions, true);
	}
}
