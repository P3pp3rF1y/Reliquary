package xreliquary.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import xreliquary.blocks.BlockApothecaryCauldron;
import xreliquary.blocks.BlockApothecaryMortar;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.model.ModelMortar;
import xreliquary.lib.ClientReference;

import java.util.List;

public class RenderApothecaryMortar extends TileEntitySpecialRenderer {
    private ModelMortar modelMortar= new ModelMortar();

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d0, double d1, double d2, float var8) {
        if (tileEntity instanceof TileEntityMortar)
        {
            //GL11.glDisable(GL11.GL_LIGHTING);
            //GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glPushMatrix();
            GL11.glTranslatef((float) d0 + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
            ResourceLocation textureName = ClientReference.MORTAR_TEXTURE;

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureName);
            GL11.glPushMatrix();
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
            this.modelMortar.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();


            TileEntityMortar tileEntityMortar = (TileEntityMortar)tileEntity;
            ItemStack[] mortarItems = tileEntityMortar.getItemStacks();
            //handles rendering the four mortar items
            //none of these currently work
            if (mortarItems[0] != null) {
                EntityItem entityitem = new EntityItem(tileEntity.getWorldObj(), 0.0D, 0.0D, 0.0D, mortarItems[0]);

                entityitem.getEntityItem().stackSize = 1;
                entityitem.hoverStart = 0.0F;
                GL11.glPushMatrix();
                GL11.glTranslatef((float) d0 + 0.5F, (float) d1 + 1.5F, (float) d2 + 0.5F);
                RenderItem.renderInFrame = true;
                RenderManager.instance.renderEntityWithPosYaw(entityitem, 0D, 0D, 0D, 0F, 0F);
                RenderItem.renderInFrame = false;
                GL11.glPopMatrix();
            }

            //ignore these, when the first one works, the following will be identical, except with different offsets
            if (mortarItems[1] != null) {
                EntityItem item = new EntityItem(tileEntity.getWorldObj(), 0D, 0D, 0D, mortarItems[1]);
                RenderManager.instance.renderEntityWithPosYaw(item, d0 + 0.375D, d1 + 0.375D, d2 + 0.375D, 1.0F, 1.0F);
            }
            if (mortarItems[2] != null) {
                EntityItem item = new EntityItem(tileEntity.getWorldObj(), 0D, 0D, 0D, mortarItems[2]);
                RenderManager.instance.renderEntityWithPosYaw(item, d0 + 0.375D, d1 + 0.375D, d2 + 0.375D, 1.0F, 1.0F);
            }
            if (mortarItems[3] != null) {
                EntityItem item = new EntityItem(tileEntity.getWorldObj(), 0D, 0D, 0D, mortarItems[3]);
                RenderManager.instance.renderEntityWithPosYaw(item, d0 + 0.375D, d1 + 0.375D, d2 + 0.375D, 1.0F, 1.0F);
            }

            GL11.glPopMatrix();
            //GL11.glEnable(GL11.GL_CULL_FACE);
            //GL11.glEnable(GL11.GL_LIGHTING);
        }
    }
}
