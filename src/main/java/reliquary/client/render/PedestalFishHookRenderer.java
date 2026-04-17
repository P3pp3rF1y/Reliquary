package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import reliquary.api.client.IPedestalItemRenderer;

public class PedestalFishHookRenderer implements IPedestalItemRenderer {
	private static final Identifier FISH_PARTICLES = Identifier.withDefaultNamespace("textures/entity/fishing/fishing_hook.png");
	private static final RenderType ENTITY_CUTOUT = RenderTypes.entityCutout(FISH_PARTICLES);

	@Override
	public void submitRender(SubmitNodeCollector submitNodeCollector, PedestalRenderer.PedestalRenderState renderState, Object itemData, float partialTicks, PoseStack poseStack, int packedLight, int packedOverlay) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !(itemData instanceof HookRenderingData hookData)) {
			return;
		}

		double translateX = hookData.hookX - renderState.pedestalPos.getX();
		double translateY = hookData.hookY - renderState.pedestalPos.getY();
		double translateZ = hookData.hookZ - renderState.pedestalPos.getZ();

		poseStack.pushPose();
		poseStack.translate(translateX, translateY, translateZ);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().camera.rotation());
		submitNodeCollector.submitCustomGeometry(poseStack, ENTITY_CUTOUT, (pose, vertexConsumer) -> {
			addVertex(vertexConsumer, pose, packedLight, 0.0F, 0, 0, 1);
			addVertex(vertexConsumer, pose, packedLight, 1.0F, 0, 1, 1);
			addVertex(vertexConsumer, pose, packedLight, 1.0F, 1, 1, 0);
			addVertex(vertexConsumer, pose, packedLight, 0.0F, 1, 0, 0);
		});
		poseStack.popPose();

		double pedestalX = renderState.pedestalPos.getX() + 0.5D;
		double pedestalY = renderState.pedestalPos.getY() + 0.45D;
		double pedestalZ = renderState.pedestalPos.getZ() + 0.5D;

		double hookX = hookData.hookX;
		double hookY = hookData.hookY;
		double hookZ = hookData.hookZ;

		float xDiff = (float) (pedestalX - hookX);
		float yDiff = (float) (pedestalY - hookY);
		float zDiff = (float) (pedestalZ - hookZ);

		poseStack.pushPose();
		poseStack.translate(translateX, translateY, translateZ);

		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, vertexConsumer) -> {
			for (int k = 0; k < 16; ++k) {
				float start = (float) k / (float) 16;
				float end = (float) (k + 1) / (float) 16;
				stringVertex(xDiff, yDiff, zDiff, vertexConsumer, pose, start, end);
				stringVertex(xDiff, yDiff, zDiff, vertexConsumer, pose, end, start);
			}
		});
		poseStack.popPose();
	}

	@SuppressWarnings("java:S107")
	private static void addVertex(VertexConsumer vertexBuilder, PoseStack.Pose pose, int packedLight, float x, int y, int u, int v) {
		vertexBuilder
				.addVertex(pose, x - 0.5F, y - 0.5F, 0.0F)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(packedLight)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}

	private static void stringVertex(float xDiff, float yDiff, float zDiff, VertexConsumer vertexConsumer, PoseStack.Pose pose, float scale1, float scale2) {
		float x = xDiff * scale1;
		float y = yDiff * (scale1 * scale1 + scale1) * 0.5F + 0.25F;
		float z = zDiff * scale1;
		float normalX = xDiff * scale2 - x;
		float normalY = yDiff * (scale2 * scale2 + scale2) * 0.5F + 0.25F - y;
		float normalZ = zDiff * scale2 - z;
		float f6 = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
		normalX /= f6;
		normalY /= f6;
		normalZ /= f6;
		vertexConsumer.addVertex(pose, x, y, z)
				.setColor(0, 0, 0, 255)
				.setNormal(pose, normalX, normalY, normalZ)
				.setLineWidth(2);
	}

	public static class HookRenderingData {
		final double hookX;
		final double hookY;
		final double hookZ;

		public HookRenderingData(double hookX, double hookY, double hookZ) {

			this.hookX = hookX;
			this.hookY = hookY;
			this.hookZ = hookZ;
		}
	}
}
