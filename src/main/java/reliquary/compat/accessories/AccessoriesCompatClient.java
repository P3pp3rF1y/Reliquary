package reliquary.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import reliquary.init.ModItems;

import java.util.function.Supplier;

public class AccessoriesCompatClient {
	private static final AccessoryRenderer EMPTY_RENDERER = new AccessoryRenderer() {
		@Override
		public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			//noop
		}
	};
	private static final Supplier<AccessoryRenderer> EMPTY_RENDERER_SUPPLIER = () -> EMPTY_RENDERER;

	public static void registerRenderers(IEventBus modBus) {
		modBus.addListener(EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
			AccessoriesRendererRegistry.registerRenderer(ModItems.MOB_CHARM_BELT.get(), AccessoryMobCharmBeltRenderer::new);

			AccessoriesRendererRegistry.registerRenderer(ModItems.TWILIGHT_CLOAK.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.ANGELHEART_VIAL.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.ANGELIC_FEATHER.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.PHOENIX_DOWN.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.WITHERLESS_ROSE.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.INFERNAL_CLAWS.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.KRAKEN_SHELL.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.MIDAS_TOUCHSTONE.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.FORTUNE_COIN.get(), EMPTY_RENDERER_SUPPLIER);
			AccessoriesRendererRegistry.registerRenderer(ModItems.HERO_MEDALLION.get(), EMPTY_RENDERER_SUPPLIER);
		});
	}
}
