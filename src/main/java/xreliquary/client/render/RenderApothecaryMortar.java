package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.model.ModelMortar;
import xreliquary.reference.ClientReference;

public class RenderApothecaryMortar extends TileEntitySpecialRenderer<TileEntityMortar> {

	private ModelMortar modelMortar = new ModelMortar();

	@Override
	public void renderTileEntityAt(TileEntityMortar tile, double x, double y, double z, float var8, int i) {
		if(tile instanceof TileEntityMortar) {
			EnumFacing facing = EnumFacing.getHorizontal(tile.getBlockMetadata());
			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
			ResourceLocation textureName = ClientReference.MORTAR_TEXTURE;

			FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureName);

			float horizontalRotation = facing == EnumFacing.UP ? 0f : facing.getHorizontalIndex() * 90f;

			GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(horizontalRotation, 0.0F, 1.0F, 0.0F);

			float modifier = 0.0625f;
			this.modelMortar.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0f, modifier);
			this.modelMortar.pestle.render(modifier);
			this.modelMortar.pestleKnob.render(modifier);

			ItemStack[] mortarItems = tile.getItemStacks();

			if(mortarItems[0] != null) {
				EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems[0]);
				item.getEntityItem().stackSize = 1;
				item.hoverStart = 0.0F;
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.07F, 1.375F, 0.07F);
				GlStateManager.rotate(180F, 1.0F, 0F, 0F);
				GlStateManager.rotate(1F, 0F, 0F, 1.0F);
				GlStateManager.rotate(horizontalRotation - Minecraft.getMinecraft().getRenderManager().playerViewY + 3.0F, 0.0f, 1.0f, 0.0f);
				GlStateManager.scale(0.40d, 0.40d, 0.40d);
				Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
				GlStateManager.popMatrix();
			}

			if(mortarItems[1] != null) {
				EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems[1]);
				item.getEntityItem().stackSize = 1;
				item.hoverStart = 0.0F;
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.07F, 1.375F, 0.07F);
				GlStateManager.rotate(180F, 1.0F, 0F, 0F);
				GlStateManager.rotate(-1F, 0F, 0F, 1.0F);
				GlStateManager.rotate(horizontalRotation - Minecraft.getMinecraft().getRenderManager().playerViewY - 2.0F, 0.0f, 1.0f, 0.0f);
				GlStateManager.scale(0.40d, 0.40d, 0.40d);
				Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
				GlStateManager.popMatrix();
			}

			if(mortarItems[2] != null) {
				EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems[2]);
				item.getEntityItem().stackSize = 1;
				item.hoverStart = 0.0F;
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.07F, 1.375F, -0.07F);
				GlStateManager.rotate(180F, 1.0F, 0F, 0F);
				GlStateManager.rotate(-1F, 0F, 0F, 1.0F);
				GlStateManager.rotate(horizontalRotation - Minecraft.getMinecraft().getRenderManager().playerViewY - 2.0F, 0.0f, 1.0f, 0.0f);
				GlStateManager.scale(0.40d, 0.40d, 0.40d);
				Minecraft.getMinecraft().getRenderManager().doRenderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
		}
	}
}
