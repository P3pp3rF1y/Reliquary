package xreliquary.compat.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import xreliquary.blocks.BlockApothecaryMortar;

public class WailaCallbackHandler {
    public static void callbackRegister(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new DataProviderMortar(), BlockApothecaryMortar.class);
    }
}
