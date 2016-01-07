package xreliquary.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
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
