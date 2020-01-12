package xreliquary.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import xreliquary.blocks.ApothecaryMortarBlock;
import xreliquary.blocks.tile.ApothecaryMortarTileEntity;

public class ApothecaryMortarRenderer extends TileEntityRenderer<ApothecaryMortarTileEntity> {
	@Override
	public void render(ApothecaryMortarTileEntity tile, double x, double y, double z, float partialTick, int destoryStage) {
		Direction direction = tile.getBlockState().get(ApothecaryMortarBlock.FACING);
		float horizontalRotation = direction == Direction.UP ? 0f : direction.getHorizontalIndex() * 90f;

		NonNullList<ItemStack> mortarItems = tile.getItemStacks();

		GlStateManager.pushMatrix();
		GlStateManager.translatef((float) x + 0.5f, (float) y + 0.2f, (float) z + 0.5f);
		GlStateManager.rotatef(-horizontalRotation, 0f, 1f, 0f);

		renderMortarItem(() -> {
			GlStateManager.rotatef(40F, 0F, 0F, 1F);
			GlStateManager.rotatef(90F, 0F, 1F, 0F);
		}, mortarItems.get(0), -0.02F, 0F);

		renderMortarItem(() -> {
			GlStateManager.rotatef(40F, 1F, 0F, 0F);
			GlStateManager.rotatef(180F, 0F, 1F, 0F);
		}, mortarItems.get(1), 0F, 0.02F);

		renderMortarItem(() -> {
			GlStateManager.rotatef(-40F, 0F, 0F, 1F);
			GlStateManager.rotatef(270F, 0F, 1F, 0F);
		}, mortarItems.get(2), 0.02F, 0F);

		GlStateManager.popMatrix();
	}

	private void renderMortarItem(Runnable processRotationTransforms, ItemStack itemToRender, float translateX, float translateZ) {
		if (!itemToRender.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(translateX, 0F, translateZ);
			processRotationTransforms.run();
			GlStateManager.scaled(0.60d, 0.60d, 0.60d);
			RenderHelper.enableStandardItemLighting();
			Minecraft.getInstance().getItemRenderer().renderItem(itemToRender, ItemCameraTransforms.TransformType.GROUND);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}
	}
}
