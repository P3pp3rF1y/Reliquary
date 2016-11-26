package xreliquary.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.entities.shot.EntityShotBase;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderShot<T extends EntityShotBase> extends Render<T> {

	public RenderShot(RenderManager renderManager) {
		super(renderManager);
	}

	private void doRenderShot(T entityShot, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		this.bindEntityTexture(entityShot);
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.1F, 0.1F, 0.1F);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer worldrenderer = tessellator.getBuffer();
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
	public void doRender(@Nonnull T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.doRenderShot(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull T entityShot) {
		return entityShot.getShotTexture();
	}
}
