package xreliquary.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import xreliquary.entities.EntityEnderShot;
import xreliquary.lib.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEnderShot extends Render {

    public void doRenderEnderShot(EntityEnderShot entityShot, double par2,
            double par4, double par6, float par8, float par9) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.1F, 0.1F, 0.1F);
        this.loadTexture(Reference.ENDER);
        Tessellator var12 = Tessellator.instance;
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        var12.startDrawingQuads();
        var12.setNormal(0.0F, 1.0F, 0.0F);
        var12.addVertexWithUV(-0.5F, -0.25F, 0.0D, 0, 1);
        var12.addVertexWithUV(0.5F, -0.25F, 0.0D, 1, 1);
        var12.addVertexWithUV(0.5F, 0.75F, 0.0D, 1, 0);
        var12.addVertexWithUV(-0.5F, 0.75F, 0.0D, 0, 0);
        var12.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then handing it off to a worker
     * function which does the actual work. In all probabilty, the class Render
     * is generic (Render<T extends Entity) and this method has signature public
     * void doRender(T entity, double d, double d1, double d2, float f, float
     * f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(Entity par1Entity, double par2, double par4,
            double par6, float par8, float par9) {
        this.doRenderEnderShot((EntityEnderShot) par1Entity, par2, par4, par6,
                par8, par9);
    }
}