package xreliquary.handler.config;

import net.minecraftforge.common.config.ConfigCategory;
import xreliquary.client.gui.hud.HUDPosition;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;

public class HudConfiguration {
	public static void loadHudPositions() {
		Settings.HudPositions.sojournerStaff = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.SOJOURNER_STAFF, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Sojouner Staff HUD")];
		Settings.HudPositions.handgun = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.HANDGUN, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Handgun HUD")];
		Settings.HudPositions.alkahestryTome = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.ALKAHESTRY_TOME, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Alkahestry Tome HUD")];
		Settings.HudPositions.destructionCatalyst = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.DESTRUCTION_CATALYST, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Destruction Catalyst HUD")];
		Settings.HudPositions.enderStaff = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.ENDER_STAFF, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Ender Staff HUD")];
		Settings.HudPositions.iceMagusRod = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.ICE_MAGUS_ROD, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Ice Magus Rod HUD")];
		Settings.HudPositions.glacialStaff = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.GLACIAL_STAFF, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Glacial Staff HUD")];
		Settings.HudPositions.voidTear = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.VOID_TEAR, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Void Tear HUD")];
		Settings.HudPositions.midasTouchstone = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.MIDAS_TOUCHSTONE, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Midas Touchstone HUD")];
		Settings.HudPositions.harvestRod = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.HARVEST_ROD, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Harvest Rod HUD")];
		Settings.HudPositions.infernalChalice = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.INFERNAL_CHALICE, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Infernal Chalice HUD")];
		Settings.HudPositions.heroMedallion = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.HERO_MEDALLION, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Hero Medallion HUD")];
		Settings.HudPositions.pyromancerStaff = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.PYROMANCER_STAFF, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Pyromancer Staff HUD")];
		Settings.HudPositions.rendingGale = HUDPosition.values()[ConfigurationHandler.getInt(Names.Items.RENDING_GALE, Names.Configs.HUD_POSITIONS, 6, 0, 6, "Position of Rending Gale HUD")];

		ConfigCategory category = ConfigurationHandler.configuration.getCategory(Names.Configs.HUD_POSITIONS);
		category.setLanguageKey(ConfigurationHandler.getCategoryLangRef(Names.Configs.HUD_POSITIONS));
		category.setComment("Position of mode and/or item display on the screen - used by some of the tools and weapons. 0 - bottom left, 1 - left, 2 - top left, 3 - top, 4 - top right, 5 - right, 6 - bottom right");
	}
}
