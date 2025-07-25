package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import reliquary.api.client.IPedestalItemRenderer;
import reliquary.block.tile.PedestalBlockEntity;

public class PedestalFishHookRenderer implements IPedestalItemRenderer {
	private static final ResourceLocation FISH_PARTICLES = ResourceLocation.parse("textures/entity/fishing_hook.png");
	private static final RenderType ENTITY_CUTOUT = RenderType.entityCutout(FISH_PARTICLES);

	@Override
	public void doRender(PedestalBlockEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		te.getItemData().ifPresent(itemData -> renderHook(te, poseStack, buffer, packedLight, itemData));
	}

	private void renderHook(PedestalBlockEntity te, PoseStack poseStack, MultiBufferSource buffer, int packedLight, Object itemData) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || !(itemData instanceof HookRenderingData hookData)) {
			return;
		}

		double translateX = hookData.hookX - te.getBlockPos().getX();
		double translateY = hookData.hookY - te.getBlockPos().getY();
		double translateZ = hookData.hookZ - te.getBlockPos().getZ();

		poseStack.pushPose();
		poseStack.translate(translateX, translateY, translateZ);
		poseStack.pushPose();
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Axis.YN.rotationDegrees(player.yHeadRot + 180F));

		PoseStack.Pose pose = poseStack.last();
		VertexConsumer vertexBuilder = buffer.getBuffer(ENTITY_CUTOUT);

		addVertex(vertexBuilder, pose, packedLight, 0.0F, 0, 0, 1);
		addVertex(vertexBuilder, pose, packedLight, 1.0F, 0, 1, 1);
		addVertex(vertexBuilder, pose, packedLight, 1.0F, 1, 1, 0);
		addVertex(vertexBuilder, pose, packedLight, 0.0F, 1, 0, 0);
		poseStack.popPose();

		double pedestalX = te.getBlockPos().getX() + 0.5D;
		double pedestalY = te.getBlockPos().getY() + 0.45D;
		double pedestalZ = te.getBlockPos().getZ() + 0.5D;

		double hookX = hookData.hookX;
		double hookY = hookData.hookY;
		double hookZ = hookData.hookZ;

		float xDiff = (float) (pedestalX - hookX);
		float yDiff = (float) (pedestalY - hookY);
		float zDiff = (float) (pedestalZ - hookZ);

		VertexConsumer vertextConsumer2 = buffer.getBuffer(RenderType.lineStrip());
		pose = poseStack.last();

		for (int k = 0; k < 16; ++k) {
			stringVertex(xDiff, yDiff, zDiff, vertextConsumer2, pose, (float) k / (float) 16, (float) (k + 1) / (float) 16);
		}

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
		vertexConsumer.addVertex(pose.pose(), x, y, z).setColor(0, 0, 0, 255).setNormal(pose, normalX, normalY, normalZ);
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
