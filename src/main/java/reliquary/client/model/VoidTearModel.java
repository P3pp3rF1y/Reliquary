package reliquary.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import reliquary.items.VoidTearItem;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
// - a lot of methods here are deprecated, but need to be used to delegate call to the original model
public class VoidTearModel implements BakedModel {
	private final BakedModel originalModel;

	public VoidTearModel(BakedModel originalModel) {
		this.originalModel = originalModel;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		return originalModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return originalModel.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return originalModel.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return originalModel.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return originalModel.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return originalModel.getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		//noinspection deprecation
		return originalModel.getTransforms();
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				if (Screen.hasShiftDown()) {
					ItemStack containedStack = VoidTearItem.getTearContents(stack, true);
					if (!containedStack.isEmpty()) {
						BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(containedStack, world, entity, seed);
						if (!bakedModel.isCustomRenderer()) {
							return bakedModel;
						}
					}
				}

				return originalModel.getOverrides().resolve(model, stack, world, entity, seed);
			}
		};
	}

	@Override
	public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		return originalModel.applyTransform(transformType, poseStack, applyLeftHandTransform);
	}
}
