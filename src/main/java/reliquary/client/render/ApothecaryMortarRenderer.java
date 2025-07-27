package reliquary.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import reliquary.block.ApothecaryMortarBlock;
import reliquary.block.tile.ApothecaryMortarBlockEntity;

public class ApothecaryMortarRenderer implements BlockEntityRenderer<ApothecaryMortarBlockEntity> {
	@Override
	public void render(ApothecaryMortarBlockEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Direction direction = tile.getBlockState().getValue(ApothecaryMortarBlock.FACING);
		float horizontalRotation = direction == Direction.UP ? 0F : direction.get2DDataValue() * 90F;

		NonNullList<ItemStack> mortarItems = tile.getItemStacks();

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.3D, 0.5D);
		poseStack.mulPose(Axis.YN.rotationDegrees(horizontalRotation));

		renderMortarItem(poseStack, buffer, packedLight, () -> {
			poseStack.mulPose(Axis.ZP.rotationDegrees(40F));
			poseStack.mulPose(Axis.YP.rotationDegrees(90F));
		}, mortarItems.get(0), -0.09F, 0F, packedOverlay);

		renderMortarItem(poseStack, buffer, packedLight, () -> {
			poseStack.mulPose(Axis.XP.rotationDegrees(40F));
			poseStack.mulPose(Axis.YP.rotationDegrees(180F));
		}, mortarItems.get(1), 0F, 0.09F, packedOverlay);

		renderMortarItem(poseStack, buffer, packedLight, () -> {
			poseStack.mulPose(Axis.ZN.rotationDegrees(40F));
			poseStack.mulPose(Axis.YP.rotationDegrees(270F));
		}, mortarItems.get(2), 0.09F, 0F, packedOverlay);

		poseStack.popPose();
	}

	private void renderMortarItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Runnable processRotationTransforms, ItemStack itemToRender, float translateX, float translateZ, int packedOverlay) {
		if (!itemToRender.isEmpty()) {
			poseStack.pushPose();
			poseStack.translate(translateX, 0F, translateZ);
			processRotationTransforms.run();
			poseStack.scale(0.60F, 0.60F, 0.60F);
			Minecraft.getInstance().getItemRenderer().renderStatic(itemToRender, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, buffer, null, 0);
			poseStack.popPose();
		}
	}
}
