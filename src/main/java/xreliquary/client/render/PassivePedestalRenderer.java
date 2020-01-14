package xreliquary.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import xreliquary.blocks.tile.PassivePedestalTileEntity;

public class PassivePedestalRenderer extends TileEntityRenderer<PassivePedestalTileEntity> {
	@Override
	public void render(PassivePedestalTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (!te.getStackInSlot(0).isEmpty()) {
			ItemStack stack = te.getStackInSlot(0);
			GlStateManager.pushMatrix();
			float yDiff = MathHelper.sin((System.currentTimeMillis() % 86400000) / 1000F) * 0.1F + 0.1F;
			GlStateManager.translatef((float) x + 0.5F, (float) y + 0.9F + yDiff, (float) z + 0.5F);
			float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
			GlStateManager.rotatef(f3, 0.0F, 1.0F, 0.0F);
			GlStateManager.scaled(0.75d, 0.75d, 0.75d);
			RenderHelper.enableStandardItemLighting();
			Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();
		}
	}
}
