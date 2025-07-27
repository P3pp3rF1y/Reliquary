package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import reliquary.block.tile.PedestalBlockEntity;
import reliquary.client.registry.PedestalClientRegistry;

public class PedestalRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
	@Override
	public void render(PedestalBlockEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (!te.getItem().isEmpty()) {
			ItemStack stack = te.getItem();
			poseStack.pushPose();
			float yDiff = Mth.sin((System.currentTimeMillis() % 86400000) / 1000F) * 0.1F + 0.1F;
			poseStack.translate(0.5D, 0.9D + yDiff, 0.5D);
			float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
			poseStack.mulPose(Axis.YP.rotationDegrees(f3));
			poseStack.scale(0.75F, 0.75F, 0.75F);
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);
			poseStack.popPose();
			PedestalClientRegistry.getItemRenderer(stack).ifPresent(extraRenderer -> extraRenderer.doRender(te, partialTicks, poseStack, buffer, packedLight, packedOverlay));
		}
	}

	@Override
	public AABB getRenderBoundingBox(PedestalBlockEntity blockEntity) {
		BlockPos pos = blockEntity.getBlockPos();
		AABB aabb = new AABB(pos.getX() - 1, pos.getY(), pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		blockEntity.executeOnActionItem(ai -> ai.getRenderBoundingBoxOuterPosition().ifPresent(aabb::expandTowards));
		return aabb;
	}
}
