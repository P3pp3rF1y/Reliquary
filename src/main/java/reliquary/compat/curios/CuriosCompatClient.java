package reliquary.compat.curios;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import reliquary.init.ModItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosCompatClient {
	public static void registerLayerDefinitions(IEventBus modEventBus) {
		modEventBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
			CuriosRendererRegistry.register(ModItems.MOB_CHARM_BELT.get(), MobCharmBeltRenderer::new);
		});
	}
}
