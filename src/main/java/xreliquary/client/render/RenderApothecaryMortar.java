package xreliquary.client.render;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;
import xreliquary.blocks.tile.TileEntityMortar;
import xreliquary.client.model.ModelMortar;
import xreliquary.reference.ClientReference;

public class RenderApothecaryMortar extends TileEntitySpecialRenderer<TileEntityMortar> {

    //TODO: review if plainly using Minecraft.getMinecraft().getRenderManager() in place of RenderManager.instance is the
    // best way to fix this for 1.8.9
    private ModelMortar modelMortar = new ModelMortar();
    private final float modifier = 0.0625f;

    @Override
    public void renderTileEntityAt(TileEntityMortar tile, double x, double y, double z, float var8, int i) {
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

            TileEntityMortar tileEntityMortar = (TileEntityMortar) tile;
            ItemStack[] mortarItems = tileEntityMortar.getItemStacks();

            if (mortarItems[0] != null) {
                EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems[0]);
                item.getEntityItem().stackSize = 1;
                item.hoverStart = 0.0F;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.07F, 1.375F, 0.07F);
                GL11.glRotatef(180F, 1.0F, 0F, 0F);
                GL11.glRotatef(1F, 0F, 0F, 1.0F);
                GL11.glRotatef(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY + 3.0F, 0.0f, 1.0f, 0.0f);
                GL11.glScaled(0.40d, 0.40d, 0.40d);
                Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();
            }

            if (mortarItems[1] != null) {
                EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems[1]);
                item.getEntityItem().stackSize = 1;
                item.hoverStart = 0.0F;
                GL11.glPushMatrix();
                GL11.glTranslatef(0.07F, 1.375F, 0.07F);
                GL11.glRotatef(180F, 1.0F, 0F, 0F);
                GL11.glRotatef(-1F, 0F, 0F, 1.0F);
                GL11.glRotatef(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY - 2.0F, 0.0f, 1.0f, 0.0f);
                GL11.glScaled(0.40d, 0.40d, 0.40d);
                Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();
            }

            if (mortarItems[2] != null) {
                EntityItem item = new EntityItem(tile.getWorld(), 0.0D, 0.0D, 0.0D, mortarItems[2]);
                item.getEntityItem().stackSize = 1;
                item.hoverStart = 0.0F;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.07F, 1.375F, -0.07F);
                GL11.glRotatef(180F, 1.0F, 0F, 0F);
                GL11.glRotatef(-1F, 0F, 0F, 1.0F);
                GL11.glRotatef(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY - 2.0F, 0.0f, 1.0f, 0.0f);
                GL11.glScaled(0.40d, 0.40d, 0.40d);
                Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
        }
    }
}
