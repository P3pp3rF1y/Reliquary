package reliquary.compat.curios;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import reliquary.client.model.MobCharmBeltModel;
import reliquary.init.ModItems;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CuriosCompatClient {
	public static void registerLayerDefinitions(IEventBus modEventBus) {
		modEventBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
			event.registerLayerDefinition(MobCharmBeltRenderer.MOB_CHARM_BELT_LAYER, MobCharmBeltModel::createBodyLayer);
			ICurioRenderer.register(ModItems.MOB_CHARM_BELT.get(), MobCharmBeltRenderer::new);
		});
	}

	public static void registerFortuneCoinToggler() {
		new CuriosFortuneCoinToggler().registerSelf();
	}
}
