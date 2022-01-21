package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.entities.shot.ShotEntityBase;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ShotRenderer<T extends ShotEntityBase> extends EntityRenderer<T> {
	private final Map<EntityType<?>, RenderType> entityCutOuts = new HashMap<>();

	public ShotRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.scale(0.1F, 0.1F, 0.1F);
		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normal = pose.normal();
		VertexConsumer vertexBuilder = buffer.getBuffer(getEntityCutOut(entity));
		addVertex(vertexBuilder, matrix, normal, packedLight, -0.5F, -0.25F, 0, 1);
		addVertex(vertexBuilder, matrix, normal, packedLight, 0.5F, -0.25F, 1, 1);
		addVertex(vertexBuilder, matrix, normal, packedLight, 0.5F, 0.75F, 1, 0);
		addVertex(vertexBuilder, matrix, normal, packedLight, -0.5F, 0.75F, 0, 0);
		poseStack.popPose();

		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	private void addVertex(VertexConsumer vertexBuilder, Matrix4f matrix, Matrix3f normal, int packedLight, float x, float y, int u, int v) {
		vertexBuilder.vertex(matrix, x, y, (float) 0.0)
				.color(255, 255, 255, 255)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(packedLight)
				.normal(normal, 0.0F, 1.0F, 0.0F)
				.endVertex();
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
