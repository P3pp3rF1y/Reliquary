package reliquary.compat.accessories;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import reliquary.init.ModItems;

public class AccessoriesCompatClient {
	public static void registerRenderers(IEventBus modBus) {
		modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
			AccessoriesRendererRegistry.registerRenderer(ModItems.MOB_CHARM_BELT.getId(), AccessoryMobCharmBeltRenderer::new);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.MOB_CHARM_BELT.get(), ModItems.MOB_CHARM_BELT.getId());

			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.TWILIGHT_CLOAK.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.ANGELHEART_VIAL.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.ANGELIC_FEATHER.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.PHOENIX_DOWN.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.WITHERLESS_ROSE.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.INFERNAL_CLAWS.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.KRAKEN_SHELL.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.MIDAS_TOUCHSTONE.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.FORTUNE_COIN.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
			AccessoriesRendererRegistry.bindItemToRenderer(ModItems.HERO_MEDALLION.get(), AccessoriesRendererRegistry.NO_RENDERER_ID);
		});
	}
}
