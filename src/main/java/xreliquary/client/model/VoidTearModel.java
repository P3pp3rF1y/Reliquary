package xreliquary.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.items.VoidTearItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
// - a lot of methods here are deprecated, but need to be used to delegate call to the original model
public class VoidTearModel implements IBakedModel {
	private final IBakedModel originalModel;

	public VoidTearModel(IBakedModel originalModel) {

		this.originalModel = originalModel;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		return originalModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return originalModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return originalModel.isGui3d();
	}

	@Override
	public boolean func_230044_c_() {
		return originalModel.func_230044_c_();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return originalModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return originalModel.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		//noinspection deprecation
		return originalModel.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				if (Screen.hasShiftDown()) {
					ItemStack containedStack = ModItems.VOID_TEAR.getContainerItem(stack, true);
					if (!containedStack.isEmpty()) {
						IBakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(containedStack, world, entity);
						if (!bakedModel.isBuiltInRenderer()) {
							return bakedModel;
						}
					}
				}

				return originalModel.getOverrides().getModelWithOverrides(model, stack, world, entity);
			}
		};
	}

	@Override
	public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
		return originalModel.handlePerspective(cameraTransformType, mat);
	}
}
