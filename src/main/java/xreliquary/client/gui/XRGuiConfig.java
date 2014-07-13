package xreliquary.client.gui;

import java.util.List;

import xreliquary.Reliquary;
import xreliquary.lib.Reference;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class XRGuiConfig extends GuiConfig {

	public XRGuiConfig(GuiScreen parent) {
		super(parent, Reliquary.CONFIG.toGui(Reference.MOD_ID), Reference.MOD_ID, true, false, Reliquary.CONFIG.getFile().getName());
	}

}
