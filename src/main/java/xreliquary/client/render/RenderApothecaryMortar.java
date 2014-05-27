package xreliquary.client.render;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.model.ModelMortar;
import xreliquary.lib.ClientReference;

public class RenderApothecaryMortar extends TileEntitySpecialRenderer {

    private ModelMortar modelMortar = new ModelMortar();
    private RenderItem renderItem = new RenderItem();
    private final float modifier = 0.0625f;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float var8) {
        if (tile instanceof TileEntityMortar) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
            ResourceLocation textureName = ClientReference.MORTAR_TEXTURE;

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(textureName);
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

            this.modelMortar.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, modifier);
            // TODO: Paving the way for the model rendering.
            this.modelMortar.pestle.render(modifier);
            this.modelMortar.pestleKnob.render(modifier);

            TileEntityMortar tileEntityMortar = (TileEntityMortar)tile;
            ItemStack[] mortarItems = tileEntityMortar.getItemStacks();

            if (mortarItems[0] != null) {
                EntityItem item = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, mortarItems[0]);
                item.getEntityItem().stackSize = 1;
                item.hoverStart = 0.0F;
                GL11.glPushMatrix();
                GL11.glTranslatef(0F, 1.375F, -0.06F);
                GL11.glRotatef(180F, 1.0F, 0F, 0F);
                GL11.glRotatef(1F, 0F, 0F, 1.0F);
                GL11.glRotatef(180.0F - RenderManager.instance.playerViewY + 3.0F, 0.0f, 1.0f, 0.0f);
                GL11.glScaled(0.94d, 0.94d, 0.94d);
                RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();
            }

            if (mortarItems[1] != null) {
                EntityItem item = new EntityItem(tile.getWorldObj(), 0.0D, 0.0D, 0.0D, mortarItems[1]);
                item.getEntityItem().stackSize = 1;
                item.hoverStart = 0.0F;
                GL11.glPushMatrix();
                GL11.glTranslatef(0F, 1.375F, 0.06F);
                GL11.glRotatef(180F, 1.0F, 0F, 0F);
                GL11.glRotatef(-1F, 0F, 0F, 1.0F);
                GL11.glRotatef(180.0F - RenderManager.instance.playerViewY - 2.0F, 0.0f, 1.0f, 0.0f);
                GL11.glScaled(0.94d, 0.94d, 0.94d);
                RenderManager.instance.renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }
    }
}
