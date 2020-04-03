package xreliquary.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import xreliquary.blocks.ApothecaryMortarBlock;
import xreliquary.blocks.tile.ApothecaryMortarTileEntity;

public class ApothecaryMortarRenderer extends TileEntityRenderer<ApothecaryMortarTileEntity> {
	public ApothecaryMortarRenderer(TileEntityRendererDispatcher tile) {
		super(tile);
	}

	@Override
	public void render(ApothecaryMortarTileEntity tile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		Direction direction = tile.getBlockState().get(ApothecaryMortarBlock.FACING);
		float horizontalRotation = direction == Direction.UP ? 0F : direction.getHorizontalIndex() * 90F;

		NonNullList<ItemStack> mortarItems = tile.getItemStacks();

		matrixStack.push();
		matrixStack.translate(0.5D, 0.3D, 0.5D);
		matrixStack.rotate(Vector3f.YN.rotationDegrees(horizontalRotation));

		renderMortarItem(matrixStack, buffer, packedLight, () -> {
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(40F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(90F));
		}, mortarItems.get(0), -0.09F, 0F);

		renderMortarItem(matrixStack, buffer, packedLight, () -> {
			matrixStack.rotate(Vector3f.XP.rotationDegrees(40F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180F));
		}, mortarItems.get(1), 0F, 0.09F);

		renderMortarItem(matrixStack, buffer, packedLight, () -> {
			matrixStack.rotate(Vector3f.ZN.rotationDegrees(40F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(270F));
		}, mortarItems.get(2), 0.09F, 0F);

		matrixStack.pop();
	}

	private void renderMortarItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, Runnable processRotationTransforms, ItemStack itemToRender, float translateX, float translateZ) {
		if (!itemToRender.isEmpty()) {
			matrixStack.push();
			matrixStack.translate(translateX, 0F, translateZ);
			processRotationTransforms.run();
			matrixStack.scale(0.60F, 0.60F, 0.60F);
			RenderHelper.enableStandardItemLighting();
			Minecraft.getInstance().getItemRenderer().renderItem(itemToRender, ItemCameraTransforms.TransformType.GROUND, packedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
			RenderHelper.disableStandardItemLighting();
			matrixStack.pop();
		}
	}
}
