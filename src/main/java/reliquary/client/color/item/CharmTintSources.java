package reliquary.client.color.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import reliquary.item.MobCharmItem;

import javax.annotation.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CharmTintSources {
	private static final Cache<EntityType<?>, Integer> entityTypeMainTints = CacheBuilder.newBuilder().expireAfterAccess(2L, TimeUnit.HOURS).build();
	private static final Cache<EntityType<?>, Integer> entityTypeAccentTints = CacheBuilder.newBuilder().expireAfterAccess(2L, TimeUnit.HOURS).build();

	public static class Main implements ItemTintSource {
		public static final Main INSTANCE = new Main();
		public static final MapCodec<Main> MAP_CODEC = MapCodec.unit(INSTANCE);

		@Override
		public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
			return MobCharmItem.getEntityTypeFromCharm(itemStack).map(entityType -> getTint(clientLevel, livingEntity, entityType, entityTypeMainTints))
					.orElse(-1);
		}

		@Override
		public MapCodec<? extends ItemTintSource> type() {
			return MAP_CODEC;
		}
	}

	public static class Accent implements ItemTintSource {
		public static final Accent INSTANCE = new Accent();
		public static final MapCodec<Accent> MAP_CODEC = MapCodec.unit(INSTANCE);

		@Override
		public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
			return MobCharmItem.getEntityTypeFromCharm(itemStack).map(entityType -> getTint(clientLevel, livingEntity, entityType, entityTypeAccentTints))
					.orElse(-1);
		}

		@Override
		public MapCodec<? extends ItemTintSource> type() {
			return MAP_CODEC;
		}
	}

	private static int getTint(@Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, EntityType<?> entityType,
			Cache<EntityType<?>, Integer> tintCache) {
		Integer tint = tintCache.getIfPresent(entityType);

		if (tint != null) {
			return tint;
		}

		getLayerRenderState(entityType, clientLevel, livingEntity).ifPresentOrElse(layer -> cacheTints(layer, entityType), () -> tintCache.put(entityType, -1));

		return tintCache.getIfPresent(entityType);
	}

	private static void cacheTints(ItemStackRenderState.LayerRenderState layer, EntityType<?> entityType) {
		if (layer.tintLayers.length > 1) {
			entityTypeMainTints.put(entityType, layer.tintLayers[0]);
			entityTypeAccentTints.put(entityType, layer.tintLayers[1]);
			return;
		}

		List<BakedQuad> quads = layer.prepareQuadList();
		if (!quads.isEmpty()) {
			try {
				NativeImage img = quads.getFirst().sprite().contents().getOriginalImage();
				int[] pixels = img.getPixels();
				List<Color> colors = ColorAnalyzer.getMainAndAccentColors(pixels, img.getWidth(), img.getHeight());
				if (colors.size() > 1) {
					entityTypeMainTints.put(entityType, ARGB.opaque(colors.get(0).getRGB()));
					entityTypeAccentTints.put(entityType, ARGB.opaque(colors.get(1).getRGB()));
					return;
				}
			} catch (Exception e) {
				// just ignore and fallback to -1
			}
		}
		entityTypeMainTints.put(entityType, -1);
		entityTypeAccentTints.put(entityType, -1);
	}

	private static Optional<ItemStackRenderState.LayerRenderState> getLayerRenderState(EntityType<?> entityType, @Nullable ClientLevel clientLevel,
			@Nullable LivingEntity livingEntity) {
		SpawnEggItem eggItem = SpawnEggItem.byId(entityType);
		if (eggItem == null) {
			return Optional.empty();
		}

		ItemStack egg = new ItemStack(eggItem);

		ItemStackRenderState renderState = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState, egg, ItemDisplayContext.GUI, clientLevel, livingEntity, 0);
		if (renderState.layers.length == 0) {
			return Optional.empty();
		}

		ItemStackRenderState.LayerRenderState layer = renderState.layers[0];
		return Optional.of(layer);
	}
}
