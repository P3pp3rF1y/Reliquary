package xreliquary.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.ArrayList;
import java.util.List;

public class ModGuiConfig extends GuiConfig {
	public ModGuiConfig(GuiScreen guiScreen) {
		super(guiScreen, getConfigElements(), Reference.MOD_ID, false, false, I18n.translateToLocal("config.title1"));
	}

	private static List<IConfigElement> getConfigElements() {
		ArrayList<IConfigElement> elements = new ArrayList<>();
		elements.add(getCategory(Names.easy_mode_recipes));
		elements.add(getCategory(Names.item_and_block_settings));
		elements.add(getCategory(Names.hud_positions));
		elements.add(getCategory(Names.mob_drop_probability));
		elements.add(getCategory(Names.potion_map));
		elements.addAll(getCategory("general").getChildElements());

		return elements;
	}

	private static ConfigElement getCategory(String category) {
		return new ConfigElement(ConfigurationHandler.configuration.getCategory(category));
	}

}
