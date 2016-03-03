package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.init.ModItems;

public class TileEntityPedestalRenderer extends TileEntitySpecialRenderer<TileEntityPedestal> {

    @Override
    public void renderTileEntityAt(TileEntityPedestal te, double x, double y, double z, float partialTicks, int destroyStage) {
        EntityItem item = new EntityItem(te.getWorld(), 0.0D, 0.0D, 0.0D, new ItemStack(ModItems.fortuneCoin));
        item.getEntityItem().stackSize = 1;
        item.hoverStart = 0.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.2F, (float) z + 0.5F);
        GlStateManager.rotate(180F, 1.0F, 0F, 0F);
        GlStateManager.rotate(1F, 0F, 0F, 1.0F);
        GlStateManager.scale(0.75d, 0.75d, 0.75d);
        Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        GlStateManager.popMatrix();
    }
}
