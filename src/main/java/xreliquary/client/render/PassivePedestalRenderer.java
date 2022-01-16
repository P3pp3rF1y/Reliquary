package xreliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import xreliquary.blocks.tile.PassivePedestalBlockEntity;

public class PassivePedestalRenderer implements BlockEntityRenderer<PassivePedestalBlockEntity> {
	@Override
	public void render(PassivePedestalBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!te.getItem().isEmpty()) {
			ItemStack stack = te.getItem();
			matrixStack.pushPose();
			float yDiff = Mth.sin((System.currentTimeMillis() % 86400000) / 1000F) * 0.1F + 0.1F;
			matrixStack.translate(0.5D, 0.9D + yDiff, 0.5D);
			float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(f3));
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 0);
			matrixStack.popPose();
		}
	}
}
