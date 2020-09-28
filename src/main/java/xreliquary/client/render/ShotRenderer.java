package xreliquary.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.shot.ShotEntityBase;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ShotRenderer<T extends ShotEntityBase> extends EntityRenderer<T> {
	private final Map<EntityType<?>, RenderType> entityCutOuts = new HashMap<>();

	public ShotRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
		matrixStack.push();
		matrixStack.scale(0.1F, 0.1F, 0.1F);
		MatrixStack.Entry matrixStackEntry = matrixStack.getLast();
		Matrix4f matrix = matrixStackEntry.getMatrix();
		Matrix3f normal = matrixStackEntry.getNormal();
		IVertexBuilder vertexBuilder = buffer.getBuffer(getEntityCutOut(entity));
		addVertex(vertexBuilder, matrix, normal, packedLight, -0.5F, -0.25F, 0, 1);
		addVertex(vertexBuilder, matrix, normal, packedLight, 0.5F, -0.25F, 1, 1);
		addVertex(vertexBuilder, matrix, normal, packedLight, 0.5F, 0.75F, 1, 0);
		addVertex(vertexBuilder, matrix, normal, packedLight, -0.5F, 0.75F, 0, 0);
		matrixStack.pop();

		super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
	}

	private void addVertex(IVertexBuilder vertexBuilder, Matrix4f matrix, Matrix3f normal, int packedLight, float x, float y, int u, int v) {
		vertexBuilder.pos(matrix, x, y, (float) 0.0)
				.color(255, 255, 255, 255)
				.tex(u, v)
				.overlay(OverlayTexture.NO_OVERLAY)
				.lightmap(packedLight)
				.normal(normal, 0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	private RenderType getEntityCutOut(T entity) {
		if (!entityCutOuts.containsKey(entity.getType())) {
			entityCutOuts.put(entity.getType(), RenderType.getEntityCutout(entity.getShotTexture()));
		}
		return entityCutOuts.get(entity.getType());
	}

	@Override
	public ResourceLocation getEntityTexture(T entityShot) {
		return entityShot.getShotTexture();
	}
}
