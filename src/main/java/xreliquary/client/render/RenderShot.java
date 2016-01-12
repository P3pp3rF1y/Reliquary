package xreliquary.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.entities.shot.EntityShotBase;
import xreliquary.lib.ClientReference;

@SideOnly(Side.CLIENT)
public class RenderShot extends Render<EntityShotBase> {

    public RenderShot(RenderManager renderManager) {
        super(renderManager);
    }

    public void doRenderShot(EntityShotBase entityShot, double par2, double par4, double par6, float par8, float par9) {
        this.bindEntityTexture(entityShot);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.1F, 0.1F, 0.1F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        worldrenderer.pos(-0.5F, -0.25F, 0.0D).tex(0, 1).normal(0.0F, 1.0F, 0.0F);
        worldrenderer.pos(0.5F, -0.25F, 0.0D).tex(1, 1).normal(0.0F, 1.0F, 0.0F);
        worldrenderer.pos(0.5F, 0.75F, 0.0D).tex(1, 0).normal(0.0F, 1.0F, 0.0F);
        worldrenderer.pos(-0.5F, 0.75F, 0.0D).tex(0, 0).normal(0.0F, 1.0F, 0.0F);
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    @Override
    public void doRender(EntityShotBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.doRenderShot((EntityShotBase) entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityShotBase entityShotBase) {
        return entityShotBase.getShotTexture();
    }
}