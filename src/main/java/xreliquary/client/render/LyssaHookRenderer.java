package xreliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import xreliquary.entities.LyssaHook;
import xreliquary.init.ModItems;

public class LyssaHookRenderer extends EntityRenderer<LyssaHook> {
	private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/entity/fishing_hook.png");
	private static final RenderType ENTITY_CUTOUT = RenderType.entityCutout(FISH_PARTICLES);

	public LyssaHookRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(LyssaHook entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		entity.getFishingPlayerOptional().ifPresent(player -> {
			matrixStack.pushPose();
			matrixStack.pushPose();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			PoseStack.Pose pose1 = matrixStack.last();
			Matrix4f matrix4f = pose1.pose();
			Matrix3f matrix3f = pose1.normal();
			VertexConsumer vertexconsumer = buffer.getBuffer(ENTITY_CUTOUT);
			vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1);
			vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1);
			vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0);
			vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0);
			matrixStack.popPose();
			int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
			ItemStack itemstack = player.getMainHandItem();
			if (!itemstack.is(ModItems.ROD_OF_LYSSA.get())) {
				i = -i;
			}

			float f = player.getAttackAnim(partialTicks);
			float f1 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
			float f2 = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float) Math.PI / 180F);
			double d0 = Mth.sin(f2);
			double d1 = Mth.cos(f2);
			double d2 = i * 0.35D;
			double d4;
			double d5;
			double d6;
			float f3;
			if ((entityRenderDispatcher.options == null || entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
				double d7 = 960.0D / entityRenderDispatcher.options.fov;
				Vec3 vec3 = entityRenderDispatcher.camera.getNearPlane().getPointOnPlane(i * 0.525F, -0.1F);
				vec3 = vec3.scale(d7);
				vec3 = vec3.yRot(f1 * 0.5F);
				vec3 = vec3.xRot(-f1 * 0.7F);
				d4 = Mth.lerp(partialTicks, player.xo, player.getX()) + vec3.x;
				d5 = Mth.lerp(partialTicks, player.yo, player.getY()) + vec3.y;
				d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) + vec3.z;
				f3 = player.getEyeHeight();
			} else {
				d4 = Mth.lerp(partialTicks, player.xo, player.getX()) - d1 * d2 - d0 * 0.8D;
				d5 = player.yo + player.getEyeHeight() + (player.getY() - player.yo) * partialTicks - 0.45D;
				d6 = Mth.lerp(partialTicks, player.zo, player.getZ()) - d0 * d2 + d1 * 0.8D;
				f3 = player.isCrouching() ? -0.1875F : 0.0F;
			}

			double d9 = Mth.lerp(partialTicks, entity.xo, entity.getX());
			double d10 = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
			double d8 = Mth.lerp(partialTicks, entity.zo, entity.getZ());
			float f4 = (float) (d4 - d9);
			float f5 = (float) (d5 - d10) + f3;
			float f6 = (float) (d6 - d8);
			VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.lineStrip());
			PoseStack.Pose pose2 = matrixStack.last();

			for (int k = 0; k <= 16; ++k) {
				stringVertex(f4, f5, f6, vertexconsumer1, pose2, sixteenthOf(k), sixteenthOf(k + 1));
			}

			matrixStack.popPose();
			super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
		});
	}

	private static float sixteenthOf(int number) {
		return (float) number / (float) 16;
	}

	private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight, float x, int y, int u, int v) {
		vertexConsumer.vertex(matrix4f, x - 0.5F, y - 0.5F, 0.0F).color(255, 255, 255, 255)
				.uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	private static void stringVertex(float x, float y, float z, VertexConsumer vertexConsumer, PoseStack.Pose pose, float scale1, float scale2) {
		float f = x * scale1;
		float f1 = y * (scale1 * scale1 + scale1) * 0.5F + 0.25F;
		float f2 = z * scale1;
		float f3 = x * scale2 - f;
		float f4 = y * (scale2 * scale2 + scale2) * 0.5F + 0.25F - f1;
		float f5 = z * scale2 - f2;
		float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
		f3 /= f6;
		f4 /= f6;
		f5 /= f6;
		vertexConsumer.vertex(pose.pose(), f, f1, f2).color(0, 0, 0, 255).normal(pose.normal(), f3, f4, f5).endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(LyssaHook entity) {
		return FISH_PARTICLES;
	}
}
