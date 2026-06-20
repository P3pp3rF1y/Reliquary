package reliquary.compat.curios;

import net.neoforged.bus.api.IEventBus;

public class CuriosCompatClient {
	public static void registerLayerDefinitions(IEventBus modEventBus) {
		// Curios has no 26.2 release yet; its 26.1 renderer API still references removed rendering buffers.
	}
}
