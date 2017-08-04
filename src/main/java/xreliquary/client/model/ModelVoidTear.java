package xreliquary.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import xreliquary.init.ModItems;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import java.util.List;

public class ModelVoidTear implements IBakedModel {
	private final IBakedModel originalModel;

	public ModelVoidTear(IBakedModel originalModel) {

		this.originalModel = originalModel;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
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
	public boolean isBuiltInRenderer() {
		return originalModel.isBuiltInRenderer();
	}

	@Nonnull
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return originalModel.getParticleTexture();
	}

	@Nonnull
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		//noinspection deprecation
		return originalModel.getItemCameraTransforms();
	}

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList(ImmutableList.of()) {
			@Nonnull
			@Override
			public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					ItemStack containedStack = ModItems.voidTear.getContainerItemClient(stack);
					if(!containedStack.isEmpty()) {
						IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(containedStack, world, entity);
						if(!bakedModel.isBuiltInRenderer())
							return bakedModel;
					}
				}

				return ModelVoidTear.this;
			}
		};
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		return originalModel.handlePerspective(cameraTransformType);
	}
}
