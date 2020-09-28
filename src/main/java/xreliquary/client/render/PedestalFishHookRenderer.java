package xreliquary.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import xreliquary.api.client.IPedestalItemRenderer;
import xreliquary.blocks.tile.PedestalTileEntity;

public class PedestalFishHookRenderer implements IPedestalItemRenderer {
	private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/entity/fishing_hook.png");
	private static final RenderType ENTITY_CUTOUT = RenderType.getEntityCutout(FISH_PARTICLES);

	@Override
	public void doRender(PedestalTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		te.getItemData().ifPresent(itemData -> renderHook(te, matrixStack, buffer, packedLight, itemData));
	}

	private void renderHook(PedestalTileEntity te, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, Object itemData) {
		if (!(itemData instanceof HookRenderingData)) {
			return;
		}

		HookRenderingData hookData = (HookRenderingData) itemData;
		double translateX = hookData.hookX - te.getPos().getX();
		double translateY = hookData.hookY - te.getPos().getY();
		double translateZ = hookData.hookZ - te.getPos().getZ();

		matrixStack.push();
		matrixStack.translate(translateX, translateY, translateZ);
		matrixStack.push();
		matrixStack.scale(0.5F, 0.5F, 0.5F);
		matrixStack.rotate(Vector3f.YN.rotationDegrees(Minecraft.getInstance().player.rotationYawHead + 180F));

		MatrixStack.Entry matrixStackEntry = matrixStack.getLast();
		Matrix4f matrix = matrixStackEntry.getMatrix();
		Matrix3f normal = matrixStackEntry.getNormal();
		IVertexBuilder vertexBuilder = buffer.getBuffer(ENTITY_CUTOUT);

		addVertex(vertexBuilder, matrix, normal, packedLight, 0.0F, 0, 0, 1);
		addVertex(vertexBuilder, matrix, normal, packedLight, 1.0F, 0, 1, 1);
		addVertex(vertexBuilder, matrix, normal, packedLight, 1.0F, 1, 1, 0);
		addVertex(vertexBuilder, matrix, normal, packedLight, 0.0F, 1, 0, 0);
		matrixStack.pop();

		double pedestalX = te.getPos().getX() + 0.5D;
		double pedestalY = te.getPos().getY() + 0.45D;
		double pedestalZ = te.getPos().getZ() + 0.5D;

		double hookX = hookData.hookX;
		double hookY = hookData.hookY;
		double hookZ = hookData.hookZ;

		float xDiff = (float) (pedestalX - hookX);
		float yDiff = (float) (pedestalY - hookY);
		float zDiff = (float) (pedestalZ - hookZ);

		IVertexBuilder vertexBuilder2 = buffer.getBuffer(RenderType.getLines());
		Matrix4f lastMatrix = matrixStack.getLast().getMatrix();

		for (int k = 0; k < 16; ++k) {
			addVertex(xDiff, yDiff, zDiff, vertexBuilder2, lastMatrix, (float) k / (float) 16);
			addVertex(xDiff, yDiff, zDiff, vertexBuilder2, lastMatrix, (float) (k + 1) / (float) 16);
		}

		matrixStack.pop();
	}

	private static void addVertex(IVertexBuilder vertexBuilder, Matrix4f matrix, Matrix3f normal, int packedLight, float x, int y, int u, int v) {
		vertexBuilder
				.pos(matrix, x - 0.5F, (float) y - 0.5F, 0.0F)
				.color(255, 255, 255, 255)
				.tex((float) u, (float) v)
				.overlay(OverlayTexture.NO_OVERLAY)
				.lightmap(packedLight)
				.normal(normal, 0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	private static void addVertex(float x, float y, float z, IVertexBuilder vertexBuilder, Matrix4f matrix, float scale) {
		vertexBuilder
				.pos(matrix, x * scale, y * (scale * scale + scale) * 0.5F + 0.25F, z * scale)
				.color(0, 0, 0, 255)
				.endVertex();
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
