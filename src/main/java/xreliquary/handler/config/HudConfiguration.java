package xreliquary.handler.config;

import net.minecraftforge.common.config.ConfigCategory;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class HudConfiguration {
	public static void loadHudPositions() {
		Settings.HudPositions.sojournerStaff = ConfigurationHandler.getInt(Names.Items.SOJOURNER_STAFF, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Sojouner Staff HUD");
		Settings.HudPositions.handgun = ConfigurationHandler.getInt(Names.Items.HANDGUN, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Handgun HUD");
		Settings.HudPositions.alkahestryTome = ConfigurationHandler.getInt(Names.Items.ALKAHESTRY_TOME, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Alkahestry Tome HUD");
		Settings.HudPositions.destructionCatalyst = ConfigurationHandler.getInt(Names.Items.DESTRUCTION_CATALYST, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Destruction Catalyst HUD");
		Settings.HudPositions.enderStaff = ConfigurationHandler.getInt(Names.Items.ENDER_STAFF, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Ender Staff HUD");
		Settings.HudPositions.iceMagusRod = ConfigurationHandler.getInt(Names.Items.ICE_MAGUS_ROD, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Ice Magus Rod HUD");
		Settings.HudPositions.glacialStaff = ConfigurationHandler.getInt(Names.Items.GLACIAL_STAFF, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Glacial Staff HUD");
		Settings.HudPositions.voidTear = ConfigurationHandler.getInt(Names.Items.VOID_TEAR, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Void Tear HUD");
		Settings.HudPositions.midasTouchstone = ConfigurationHandler.getInt(Names.Items.MIDAS_TOUCHSTONE, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Midas Touchstone HUD");
		Settings.HudPositions.harvestRod = ConfigurationHandler.getInt(Names.Items.HARVEST_ROD, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Harvest Rod HUD");
		Settings.HudPositions.infernalChalice = ConfigurationHandler.getInt(Names.Items.INFERNAL_CHALICE, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Infernal Chalice HUD");
		Settings.HudPositions.heroMedallion = ConfigurationHandler.getInt(Names.Items.HERO_MEDALLION, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Hero Medallion HUD");
		Settings.HudPositions.pyromancerStaff = ConfigurationHandler.getInt(Names.Items.PYROMANCER_STAFF, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Pyromancer Staff HUD");
		Settings.HudPositions.rendingGale = ConfigurationHandler.getInt(Names.Items.RENDING_GALE, Names.Configs.HUD_POSITIONS, 3, 0, 3, "Position of Rending Gale HUD");

		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.Configs.HUD_POSITIONS);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(Names.Configs.HUD_POSITIONS));
		category.setComment("Position of mode and/or item display on the screen - used by some of the tools and weapons. 0 - top left, 1 - top right, 2 - bottom left, 3 - bottom right");
	}
}
