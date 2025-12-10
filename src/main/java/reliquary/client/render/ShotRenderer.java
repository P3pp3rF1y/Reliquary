package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import reliquary.entity.shot.ShotBase;

public class ShotRenderer<T extends ShotBase> extends EntityRenderer<T, EntityRenderState> {
	private final ResourceLocation texture;

	public ShotRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
		super(context);
		this.texture = texture;
	}

	@Override
	public void submit(EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		poseStack.pushPose();
		poseStack.scale(0.1F, 0.1F, 0.1F);
		poseStack.mulPose(cameraRenderState.orientation);
		submitNodeCollector.submitCustomGeometry(poseStack, RenderType.entityCutout(texture), (pose, vertexConsumer) -> {
			addVertex(vertexConsumer, pose, renderState.lightCoords, -0.5F, -0.25F, 0, 1);
			addVertex(vertexConsumer, pose, renderState.lightCoords, 0.5F, -0.25F, 1, 1);
			addVertex(vertexConsumer, pose, renderState.lightCoords, 0.5F, 0.75F, 1, 0);
			addVertex(vertexConsumer, pose, renderState.lightCoords, -0.5F, 0.75F, 0, 0);
		});
		poseStack.popPose();

		super.submit(renderState, poseStack, submitNodeCollector, cameraRenderState);
	}

	@Override
	public EntityRenderState createRenderState() {
		return new EntityRenderState();
	}

	private void addVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, int packedLight, float x, float y, int u, int v) {
		vertexConsumer.addVertex(pose, x, y, (float) 0.0)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(packedLight)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}
}
