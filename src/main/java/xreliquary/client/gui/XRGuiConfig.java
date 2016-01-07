package xreliquary.client.gui;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import xreliquary.Reliquary;
import xreliquary.lib.Reference;

public class XRGuiConfig extends GuiConfig {

    public XRGuiConfig(GuiScreen parent) {
        super(parent, Reliquary.CONFIG.toGui(Reference.MOD_ID), Reference.MOD_ID, true, false, Reliquary.CONFIG.getFile().getName());
    }

}
