package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import xreliquary.blocks.tile.TileEntityMortar;

public class RenderApothecaryMortar extends TileEntitySpecialRenderer<TileEntityMortar> {

	@Override
	public void renderTileEntityAt(TileEntityMortar tile, double x, double y, double z, float var8, int i) {
		EnumFacing facing = EnumFacing.getHorizontal(tile.getBlockMetadata());
		float horizontalRotation = facing == EnumFacing.UP ? 0f : facing.getHorizontalIndex() * 90f;

		NonNullList<ItemStack> mortarItems = tile.getItemStacks();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5f, (float) y + 0.2f, (float) z + 0.5f);
		GlStateManager.rotate(-horizontalRotation, 0f, 1f, 0f);

		if(!mortarItems.get(0).isEmpty()) {
			EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems.get(0));
			item.getItem().setCount(1);
			item.hoverStart = 0.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.02F, 0F, 0F);
			GlStateManager.rotate(40F, 0F, 0F, 1F);
			GlStateManager.scale(0.60d, 0.60d, 0.60d);
			GlStateManager.rotate(90F, 0F, 1F, 0F);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
			GlStateManager.popMatrix();
		}

		if(!mortarItems.get(1).isEmpty()) {
			EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems.get(1));
			item.getItem().setCount(1);
			item.hoverStart = 0.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, 0F, 0.02F);
			GlStateManager.rotate(40F, 1F, 0F, 0F);
			GlStateManager.rotate(180F, 0F, 1F, 0F);
			GlStateManager.scale(0.60d, 0.60d, 0.60d);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
			GlStateManager.popMatrix();
		}

		if(!mortarItems.get(2).isEmpty()) {
			EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems.get(2));
			item.getItem().setCount(1);
			item.hoverStart = 0.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.02F, 0F, 0F);
			GlStateManager.rotate(-40F, 0F, 0F, 1F);
			GlStateManager.rotate(270F, 0F, 1F, 0F);
			GlStateManager.scale(0.60d, 0.60d, 0.60d);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();
	}
}
