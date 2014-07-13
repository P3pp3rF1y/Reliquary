package xreliquary.client.gui;

import net.minecraft.client.gui.GuiScreen;
import xreliquary.Reliquary;
import xreliquary.lib.Reference;
import cpw.mods.fml.client.config.GuiConfig;

public class XRGuiConfig extends GuiConfig {

	public XRGuiConfig(GuiScreen parent) {
		super(parent, Reliquary.CONFIG.toGui(Reference.MOD_ID), Reference.MOD_ID, true, false, Reliquary.CONFIG.getFile().getName());
	}

}
