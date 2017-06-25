package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import xreliquary.api.client.IPedestalItemRenderer;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.client.registry.PedestalClientRegistry;

public class TileEntityPedestalRenderer extends TileEntitySpecialRenderer<TileEntityPedestal> {

	@Override
	public void renderTileEntityAt(TileEntityPedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
		if(!te.getStackInSlot(0).isEmpty()) {
			ItemStack stack = te.getStackInSlot(0);
			EntityItem item = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, stack);
			item.getItem().setCount(1);
			item.hoverStart = 0.0F;
			GlStateManager.pushMatrix();
			float yDiff = MathHelper.sin((System.currentTimeMillis() % 86400000) / 1000F) * 0.1F + 0.1F;
			GlStateManager.translate((float) x + 0.5F, (float) y + 0.65F + yDiff, (float) z + 0.5F);
			float f3 = ((System.currentTimeMillis() % 86400000) / 2000F) * (180F / (float) Math.PI);
			GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(0.75d, 0.75d, 0.75d);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
			GlStateManager.popMatrix();

			IPedestalItemRenderer extraRenderer = PedestalClientRegistry.getItemRenderer(stack);
			if(extraRenderer == null && stack.getItem() instanceof IPedestalItemRenderer) {
				extraRenderer = (IPedestalItemRenderer) stack.getItem();
			}

			if(extraRenderer != null) {
				extraRenderer.doRender(this, te, stack, x, y, z, partialTicks, destroyStage);
			}
		}
	}
}
