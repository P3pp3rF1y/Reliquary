package xreliquary.handler.config;

import net.minecraftforge.common.config.ConfigCategory;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class HudConfiguration {
	public static void loadHudPositions() {
		Settings.HudPositions.sojournerStaff = ConfigurationHandler.getInt(Names.sojourner_staff, Names.hud_positions, 3, 0, 3, "Position of Sojouner Staff HUD");
		Settings.HudPositions.handgun = ConfigurationHandler.getInt(Names.handgun, Names.hud_positions, 3, 0, 3, "Position of Handgun HUD");
		Settings.HudPositions.alkahestryTome = ConfigurationHandler.getInt(Names.alkahestry_tome, Names.hud_positions, 3, 0, 3, "Position of Alkahestry Tome HUD");
		Settings.HudPositions.destructionCatalyst = ConfigurationHandler.getInt(Names.destruction_catalyst, Names.hud_positions, 3, 0, 3, "Position of Destruction Catalyst HUD");
		Settings.HudPositions.enderStaff = ConfigurationHandler.getInt(Names.ender_staff, Names.hud_positions, 3, 0, 3, "Position of Ender Staff HUD");
		Settings.HudPositions.iceMagusRod = ConfigurationHandler.getInt(Names.ice_magus_rod, Names.hud_positions, 3, 0, 3, "Position of Ice Magus Rod HUD");
		Settings.HudPositions.glacialStaff = ConfigurationHandler.getInt(Names.glacial_staff, Names.hud_positions, 3, 0, 3, "Position of Glacial Staff HUD");
		Settings.HudPositions.voidTear = ConfigurationHandler.getInt(Names.void_tear, Names.hud_positions, 3, 0, 3, "Position of Void Tear HUD");
		Settings.HudPositions.midasTouchstone = ConfigurationHandler.getInt(Names.midas_touchstone, Names.hud_positions, 3, 0, 3, "Position of Midas Touchstone HUD");
		Settings.HudPositions.harvestRod = ConfigurationHandler.getInt(Names.harvest_rod, Names.hud_positions, 3, 0, 3, "Position of Harvest Rod HUD");
		Settings.HudPositions.infernalChalice = ConfigurationHandler.getInt(Names.infernal_chalice, Names.hud_positions, 3, 0, 3, "Position of Infernal Chalice HUD");
		Settings.HudPositions.heroMedallion = ConfigurationHandler.getInt(Names.hero_medallion, Names.hud_positions, 3, 0, 3, "Position of Hero Medallion HUD");
		Settings.HudPositions.pyromancerStaff = ConfigurationHandler.getInt(Names.pyromancer_staff, Names.hud_positions, 3, 0, 3, "Position of Pyromancer Staff HUD");
		Settings.HudPositions.rendingGale = ConfigurationHandler.getInt(Names.rending_gale, Names.hud_positions, 3, 0, 3, "Position of Rending Gale HUD");

		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.hud_positions);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(Names.hud_positions));
		category.setComment("Position of mode and/or item display on the screen - used by some of the tools and weapons. 0 - top left, 1 - top right, 2 - bottom left, 3 - bottom right");
	}
}
