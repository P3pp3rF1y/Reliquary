package xreliquary.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.items.IItemHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public abstract class MobCharmBeltLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	private final BipedModel<PlayerEntity> model = new BipedModel<>(0.05F);
	private static final ResourceLocation ON_BODY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/models/armor/mob_charm_belt.png");

	protected MobCharmBeltLayerRenderer(LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount
			, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		IItemHandler baubles = getBaublesHandler(player);

		for (int i = 0; i < baubles.getSlots(); i++) {
			ItemStack bauble = baubles.getStackInSlot(i);
			if (bauble.isEmpty() || bauble.getItem() != ModItems.MOB_CHARM_BELT.get()) {
				continue;
			}

			matrixStack.push();

			if (player.isCrouching()) {
				matrixStack.translate(0D, 0.2D, 0D);
				matrixStack.rotate(Vector3f.XP.rotationDegrees(90F / (float) Math.PI));
			}

			matrixStack.translate(0D, 0.2D, 0D);

			IVertexBuilder vertexBuilder = ItemRenderer.getBuffer(buffer, RenderType.getEntityCutoutNoCull(ON_BODY_TEXTURE), false, false);
			model.bipedBody.render(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
			matrixStack.pop();
			return;
		}
	}

	protected abstract IItemHandler getBaublesHandler(PlayerEntity player);
}
