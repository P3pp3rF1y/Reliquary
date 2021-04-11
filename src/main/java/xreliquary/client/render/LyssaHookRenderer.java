package xreliquary.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import xreliquary.entities.LyssaBobberEntity;
import xreliquary.init.ModItems;

public class LyssaHookRenderer extends EntityRenderer<LyssaBobberEntity> {
	private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/entity/fishing_hook.png");
	private static final RenderType ENTITY_CUTOUT = RenderType.getEntityCutout(FISH_PARTICLES);

	public LyssaHookRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(LyssaBobberEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
		PlayerEntity playerentity = entity.getFishingPlayer();
		if (playerentity != null) {
			matrixStack.push();
			matrixStack.push();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.rotate(renderManager.getCameraOrientation());
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
			MatrixStack.Entry matrixStackEntry = matrixStack.getLast();

			Matrix4f matrix = matrixStackEntry.getMatrix();
			Matrix3f normal = matrixStackEntry.getNormal();
			IVertexBuilder vertexBuilder = buffer.getBuffer(ENTITY_CUTOUT);

			addVertex(vertexBuilder, matrix, normal, packedLight, 0.0F, 0, 0, 1);
			addVertex(vertexBuilder, matrix, normal, packedLight, 1.0F, 0, 1, 1);
			addVertex(vertexBuilder, matrix, normal, packedLight, 1.0F, 1, 1, 0);
			addVertex(vertexBuilder, matrix, normal, packedLight, 0.0F, 1, 0, 0);
			matrixStack.pop();

			int i = playerentity.getPrimaryHand() == HandSide.RIGHT ? 1 : -1;
			ItemStack itemstack = playerentity.getHeldItemMainhand();
			if (itemstack.getItem() != ModItems.ROD_OF_LYSSA.get()) {
				i = -i;
			}

			float f = playerentity.getSwingProgress(partialTicks);
			float f1 = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
			float f2 = MathHelper.lerp(partialTicks, playerentity.prevRenderYawOffset, playerentity.renderYawOffset) * ((float) Math.PI / 180F);
			double d0 = MathHelper.sin(f2);
			double d1 = MathHelper.cos(f2);
			double d2 = (double) i * 0.35D;
			double d4;
			double d5;
			double d6;
			float f3;
			if ((renderManager.options == null || renderManager.options.getPointOfView().func_243192_a()) && playerentity == Minecraft.getInstance().player) {
				double d7 = renderManager.options.fov;
				d7 = d7 / 100.0D;
				Vector3d vec3d = new Vector3d((double) i * -0.36D * d7, -0.045D * d7, 0.4D);
				vec3d = vec3d.rotatePitch(-MathHelper.lerp(partialTicks, playerentity.prevRotationPitch, playerentity.rotationPitch) * ((float) Math.PI / 180F));
				vec3d = vec3d.rotateYaw(-MathHelper.lerp(partialTicks, playerentity.prevRotationYaw, playerentity.rotationYaw) * ((float) Math.PI / 180F));
				vec3d = vec3d.rotateYaw(f1 * 0.5F);
				vec3d = vec3d.rotatePitch(-f1 * 0.7F);
				d4 = MathHelper.lerp(partialTicks, playerentity.prevPosX, playerentity.getPosX()) + vec3d.x;
				d5 = MathHelper.lerp(partialTicks, playerentity.prevPosY, playerentity.getPosY()) + vec3d.y;
				d6 = MathHelper.lerp(partialTicks, playerentity.prevPosZ, playerentity.getPosZ()) + vec3d.z;
				f3 = playerentity.getEyeHeight();
			} else {
				d4 = MathHelper.lerp(partialTicks, playerentity.prevPosX, playerentity.getPosX()) - d1 * d2 - d0 * 0.8D;
				d5 = playerentity.prevPosY + (double) playerentity.getEyeHeight() + (playerentity.getPosY() - playerentity.prevPosY) * (double) partialTicks - 0.45D;
				d6 = MathHelper.lerp(partialTicks, playerentity.prevPosZ, playerentity.getPosZ()) - d0 * d2 + d1 * 0.8D;
				f3 = playerentity.isCrouching() ? -0.1875F : 0.0F;
			}

			double d9 = MathHelper.lerp(partialTicks, entity.prevPosX, entity.getPosX());
			double d10 = MathHelper.lerp(partialTicks, entity.prevPosY, entity.getPosY()) + 0.25D;
			double d8 = MathHelper.lerp(partialTicks, entity.prevPosZ, entity.getPosZ());
			float f4 = (float) (d4 - d9);
			float f5 = (float) (d5 - d10) + f3;
			float f6 = (float) (d6 - d8);
			IVertexBuilder vertexBuilder2 = buffer.getBuffer(RenderType.getLines());
			Matrix4f matrix4f1 = matrixStack.getLast().getMatrix();

			for (int k = 0; k < 16; ++k) {
				addVertex(f4, f5, f6, vertexBuilder2, matrix4f1, (float) k / (float) 16);
				addVertex(f4, f5, f6, vertexBuilder2, matrix4f1, (float) (k + 1) / (float) 16);
			}

			matrixStack.pop();
			super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
		}
	}

	private static void addVertex(IVertexBuilder vertexBuilder, Matrix4f matrix, Matrix3f normal, int packedLight, float x, int y, int u, int v) {
		vertexBuilder.pos(matrix, x - 0.5F, (float) y - 0.5F, 0.0F).color(255, 255, 255, 255).tex((float) u, (float) v).overlay(OverlayTexture.NO_OVERLAY).lightmap(packedLight).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}

	private static void addVertex(float x, float y, float z, IVertexBuilder vertexBuilder, Matrix4f matrix, float scale) {
		vertexBuilder.pos(matrix, x * scale, y * (scale * scale + scale) * 0.5F + 0.25F, z * scale).color(0, 0, 0, 255).endVertex();
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	public ResourceLocation getEntityTexture(LyssaBobberEntity entity) {
		return FISH_PARTICLES;
	}
}
