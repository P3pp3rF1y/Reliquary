package xreliquary.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xreliquary.entities.KrakenSlimeEntity;
import xreliquary.reference.Reference;

@OnlyIn(Dist.CLIENT)
public class ThrownKrakenSlimeRenderer extends EntityRenderer<KrakenSlimeEntity> {

	private static final int TEXTURE_INDEX = 13;

	public ThrownKrakenSlimeRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	public void doRender(KrakenSlimeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		bindEntityTexture(entity);
		GlStateManager.translatef((float) x, (float) y, (float) z);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(0.5F, 0.5F, 0.5F);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		float f = (float) (TEXTURE_INDEX % 16 * 16) / 256.0F;
		float f1 = (float) (TEXTURE_INDEX % 16 * 16 + 16) / 256.0F;
		float f2 = ((float) TEXTURE_INDEX / 16 * 16 + 0) / 256.0F;
		float f3 = ((float) TEXTURE_INDEX / 16 * 16 + 16) / 256.0F;
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GlStateManager.rotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		buffer.pos(0.0F - f5, 0.0F - f6, 0.0D).tex(f, f3).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(f4 - f5, 0.0F - f6, 0.0D).tex(f1, f3).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(f4 - f5, 1.0F - f6, 0.0D).tex(f1, f2).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(0.0F - f5, 1.0F - f6, 0.0D).tex(f, f2).normal(0.0F, 1.0F, 0.0F).endVertex();
		tessellator.draw();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(KrakenSlimeEntity entity) {
		return new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + Reference.THROWN_ITEM_SPRITES);
	}

}

