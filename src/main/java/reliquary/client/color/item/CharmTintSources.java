package reliquary.client.color.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import reliquary.item.MobCharmItem;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CharmTintSources {
	private static final Cache<EntityType<?>, Integer> entityTypeMainTints = CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).build();
	private static final Cache<EntityType<?>, Integer> entityTypeAccentTints = CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).build();

	public static class Main implements ItemTintSource {
		public static final Main INSTANCE = new Main();
		public static final MapCodec<Main> MAP_CODEC = MapCodec.unit(INSTANCE);

		@Override
		public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
			return MobCharmItem.getEntityTypeFromCharm(itemStack).map(entityType -> getTint(clientLevel, livingEntity, entityType, entityTypeMainTints, 0)).orElse(-1);
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
			return MobCharmItem.getEntityTypeFromCharm(itemStack).map(entityType -> getTint(clientLevel, livingEntity, entityType, entityTypeAccentTints, 1)).orElse(-1);
		}

		@Override
		public MapCodec<? extends ItemTintSource> type() {
			return MAP_CODEC;
		}
	}

	private static int getTint(@Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, EntityType<?> entityType, Cache<EntityType<?>, Integer> tintCache, int tintIndex) {
		Integer tint = tintCache.getIfPresent(entityType);

		if (tint != null) {
			return tint;
		}


		tint = getLayerRenderState(entityType, clientLevel, livingEntity).map(layer -> layer.tintLayers.length < tintIndex + 1 ? -1 : layer.tintLayers[tintIndex]).orElse(-1);
		tintCache.put(entityType, tint);

		return tint;
	}

	private static Optional<ItemStackRenderState.LayerRenderState> getLayerRenderState(EntityType<?> entityType, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
		SpawnEggItem eggItem = SpawnEggItem.byId(entityType);
		if (eggItem == null) {
			return Optional.empty();
		}

		ItemStack egg = new ItemStack(eggItem);

		ItemStackRenderState renderState = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState, egg, ItemDisplayContext.GUI, false, clientLevel, livingEntity, 0);
		if (renderState.layers.length == 0) {
			return Optional.empty();
		}

		ItemStackRenderState.LayerRenderState layer = renderState.layers[0];
		if (layer.model == null) {
			return Optional.empty();
		}

		return Optional.of(layer);
	}
}
