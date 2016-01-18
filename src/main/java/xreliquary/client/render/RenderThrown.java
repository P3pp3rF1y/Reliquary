package xreliquary.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.lib.Reference;

public class RenderThrown extends Render<Entity> {
    //TODO: get rid of this class if not needed after all potion rendering is done

    private int itemIconIndex;

    public RenderThrown( RenderManager renderManager, int par1)
    {
        super(renderManager);
        itemIconIndex = par1;
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.bindEntityTexture(par1Entity);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float var3 = (itemIconIndex % 16 * 16 + 0) / 256.0F;
        float var4 = (itemIconIndex % 16 * 16 + 16) / 256.0F;
        float var5 = (itemIconIndex / 16 * 16 + 0) / 256.0F;
        float var6 = (itemIconIndex / 16 * 16 + 16) / 256.0F;
        float var7 = 1.0F;
        float var8 = 0.5F;
        float var9 = 0.25F;
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        worldrenderer.begin( 7, DefaultVertexFormats.POSITION_TEX_NORMAL );
        worldrenderer.pos(0.0F - var8, 0.0F - var9, 0.0D).tex(var3, var6).normal(0.0F, 1.0F, 0.0F);
        worldrenderer.pos( var7 - var8, 0.0F - var9, 0.0D).tex( var4, var6 ).normal( 0.0F, 1.0F, 0.0F );
        worldrenderer.pos( var7 - var8, var7 - var9, 0.0D).tex( var4, var5 ).normal( 0.0F, 1.0F, 0.0F );
        worldrenderer.pos( 0.0F - var8, var7 - var9, 0.0D).tex( var3, var5 ).normal( 0.0F, 1.0F, 0.0F );
        tessellator.draw();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + Reference.THROWN_ITEM_SPRITES);
    }
}
