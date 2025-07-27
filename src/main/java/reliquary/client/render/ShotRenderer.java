package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import reliquary.entity.shot.ShotBase;

import java.util.HashMap;
import java.util.Map;

public class ShotRenderer<T extends ShotBase> extends EntityRenderer<T> {
	private final Map<EntityType<?>, RenderType> entityCutOuts = new HashMap<>();

	public ShotRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.scale(0.1F, 0.1F, 0.1F);
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		PoseStack.Pose pose = poseStack.last();
		VertexConsumer vertexBuilder = buffer.getBuffer(getEntityCutOut(entity));
		addVertex(vertexBuilder, pose, packedLight, -0.5F, -0.25F, 0, 1);
		addVertex(vertexBuilder, pose, packedLight, 0.5F, -0.25F, 1, 1);
		addVertex(vertexBuilder, pose, packedLight, 0.5F, 0.75F, 1, 0);
		addVertex(vertexBuilder, pose, packedLight, -0.5F, 0.75F, 0, 0);
		poseStack.popPose();

		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private void addVertex(VertexConsumer vertexBuilder, PoseStack.Pose pose, int packedLight, float x, float y, int u, int v) {
		vertexBuilder.addVertex(pose, x, y, (float) 0.0)
				.setColor(255, 255, 255, 255)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(packedLight)
				.setNormal(pose, 0.0F, 1.0F, 0.0F);
	}

	private RenderType getEntityCutOut(T entity) {
		if (!entityCutOuts.containsKey(entity.getType())) {
			entityCutOuts.put(entity.getType(), RenderType.entityCutout(entity.getShotTexture()));
		}
		return entityCutOuts.get(entity.getType());
	}

	@Override
	public ResourceLocation getTextureLocation(T entityShot) {
		return entityShot.getShotTexture();
	}
}
