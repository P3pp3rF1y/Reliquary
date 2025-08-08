package reliquary.client.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.item.VoidTearItem;

import javax.annotation.Nullable;

public class VoidTearItemModel implements ItemModel {
	private final ItemModel empty;
	private final ItemModel filled;

	public VoidTearItemModel(ItemModel empty, ItemModel filled) {
		this.empty = empty;
		this.filled = filled;
	}

	@Override
	public void update(ItemStackRenderState renderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i) {
		renderState.appendModelIdentityElement(this);
		if (Screen.hasShiftDown() && !VoidTearItem.getTearContents(itemStack).isEmpty()) {
			ItemStack containedStack = VoidTearItem.getTearContents(itemStack);
			itemModelResolver.updateForTopItem(renderState, containedStack, itemDisplayContext, clientLevel, livingEntity, i);
			renderState.appendModelIdentityElement(containedStack);
		} else {
			(ModItems.VOID_TEAR.get().isEmpty(itemStack) ? empty : filled).update(renderState, itemStack, itemModelResolver, itemDisplayContext, clientLevel, livingEntity, i);
			renderState.appendModelIdentityElement(filled);
		}
	}

	public record Unbaked(ItemModel.Unbaked empty, ItemModel.Unbaked filled) implements ItemModel.Unbaked {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
						ItemModels.CODEC.fieldOf("empty").forGetter(Unbaked::empty),
						ItemModels.CODEC.fieldOf("filled").forGetter(Unbaked::filled))
				.apply(builder, Unbaked::new)
		);

		@Override
		public MapCodec<? extends ItemModel.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public ItemModel bake(BakingContext bakingContext) {
			return new VoidTearItemModel(empty.bake(bakingContext), filled.bake(bakingContext));
		}

		@Override
		public void resolveDependencies(Resolver resolver) {
			empty.resolveDependencies(resolver);
			filled.resolveDependencies(resolver);
		}
	}

}
