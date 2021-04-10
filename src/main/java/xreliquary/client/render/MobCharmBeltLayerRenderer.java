package xreliquary.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import xreliquary.reference.Reference;

public abstract class MobCharmBeltLayerRenderer {
	private MobCharmBeltLayerRenderer() {}
	private static final BipedModel<PlayerEntity> MODEL = new BipedModel<>(0.05F);
	private static final ResourceLocation ON_BODY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/models/armor/mob_charm_belt.png");

	public static void renderBelt(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, LivingEntity entity) {
		matrixStack.push();

		if (entity.isCrouching()) {
			matrixStack.translate(0D, 0.2D, 0D);
			matrixStack.rotate(Vector3f.XP.rotationDegrees(90F / (float) Math.PI));
		}

		matrixStack.translate(0D, 0.2D, 0D);

		IVertexBuilder vertexBuilder = ItemRenderer.getBuffer(buffer, RenderType.getEntityCutoutNoCull(ON_BODY_TEXTURE), false, false);
		MODEL.bipedBody.render(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
		matrixStack.pop();
	}
}
