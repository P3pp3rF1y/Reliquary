package xreliquary.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import xreliquary.lib.Reference;

/**
 * Created by Xeno on 9/21/2014.
 */
public class PacketHandler {


    public static SimpleNetworkWrapper networkWrapper;

    public static void init() {
        networkWrapper = new SimpleNetworkWrapper(Reference.MOD_ID);
    }
}
