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
public class RenderShot<T extends EntityShotBase> extends Render<T> {

    public RenderShot(RenderManager renderManager) {
        super(renderManager);
    }

    public void doRenderShot(T entityShot, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entityShot);
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.1F, 0.1F, 0.1F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        worldrenderer.pos(-0.5F, -0.25F, 0.0D).tex(0, 1).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(0.5F, -0.25F, 0.0D).tex(1, 1).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(0.5F, 0.75F, 0.0D).tex(1, 0).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(-0.5F, 0.75F, 0.0D).tex(0, 0).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entityShot, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.doRenderShot(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entityShot) {
        return entityShot.getShotTexture();
    }
}