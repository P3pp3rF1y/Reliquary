package xreliquary.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import xreliquary.reference.Reference;

/**
 * Created by Xeno on 9/21/2014.
 */
public class PacketHandler {

	public static SimpleNetworkWrapper networkWrapper;

	public static void init() {
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

		int idx = 0;
		networkWrapper.registerMessage(PacketFXThrownPotionImpact.class, PacketFXThrownPotionImpact.class, idx, Side.CLIENT);
		idx++;
		networkWrapper.registerMessage(PacketFXConcussiveExplosion.class, PacketFXConcussiveExplosion.class, idx, Side.CLIENT);
		idx++;
		networkWrapper.registerMessage(PacketHarvestRodCacheSync.class, PacketHarvestRodCacheSync.class, idx, Side.CLIENT);
		idx++;
		networkWrapper.registerMessage(PacketEnderStaffItemSync.class, PacketEnderStaffItemSync.class, idx, Side.CLIENT);
	}
}
