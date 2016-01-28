package xreliquary.client.gui;


import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import xreliquary.handler.ConfigurationHandler;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.ArrayList;
import java.util.List;


public class ModGuiConfig extends GuiConfig
{
	public ModGuiConfig(GuiScreen guiScreen)
	{
		super(guiScreen, getConfigElements(), Reference.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
	}

	private static List<IConfigElement> getConfigElements() {
		ArrayList<IConfigElement> elements = new ArrayList<>(  );
		elements.add(categoryElement( Names.easy_mode_recipes, Names.easy_mode_recipes));
		elements.add(categoryElement( Names.hud_positions, Names.hud_positions));
		elements.add( new ConfigElement( ConfigurationHandler.configuration.getCategory( Names.mob_drop_probability ) ));
		elements.add( new ConfigElement( ConfigurationHandler.configuration.getCategory( Names.alkahestry_tome ) ));
		elements.add( new DummyConfigElement( "Test element", "", ConfigGuiType.STRING, "test" ));

		return elements;
	}

	/** Creates a button linking to another screen where all options of the category are available */
	private static IConfigElement categoryElement(String category, String name) {
		return new DummyConfigElement.DummyCategoryElement(name, getLabelLangRef(category),
				new ConfigElement(ConfigurationHandler.configuration.getCategory(category)).getChildElements());
	}

	private static String getLabelLangRef(String category) {
		return "config." + category + ".label";
	}

}
