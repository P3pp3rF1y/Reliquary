package xreliquary.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;
import xreliquary.util.LanguageHelper;

import java.util.ArrayList;
import java.util.List;

public class ModGuiConfig extends GuiConfig {
	public ModGuiConfig(GuiScreen guiScreen) {
		super(guiScreen, getConfigElements(), Reference.MOD_ID, false, false, LanguageHelper.getLocalization("config.title1"));
	}

	private static List<IConfigElement> getConfigElements() {
		ArrayList<IConfigElement> elements = new ArrayList<>();
		elements.add(getCategory(Names.Configs.EASY_MODE_RECIPES));
		elements.add(getCategory(Names.Configs.ITEM_AND_BLOCK_SETTINGS));
		elements.add(getCategory(Names.Configs.HUD_POSITIONS));
		elements.add(getCategory(Names.Configs.POTION_MAP));
		elements.addAll(getCategory("general").getChildElements());

		return elements;
	}

	private static ConfigElement getCategory(String category) {
		return new ConfigElement(ConfigurationHandler.configuration.getCategory(category));
	}

}
