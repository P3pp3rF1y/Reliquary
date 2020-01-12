package xreliquary.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xreliquary.entities.LyssaBobberEntity;
import xreliquary.init.ModItems;

public class LyssaHookRenderer extends EntityRenderer<LyssaBobberEntity> {
	private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

	public LyssaHookRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(LyssaBobberEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		PlayerEntity playerentity = entity.getAngler();
		if (playerentity != null && !renderOutlines) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef((float) x, (float) y, (float) z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			bindEntityTexture(entity);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.rotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			if (renderOutlines) {
				GlStateManager.enableColorMaterial();
				GlStateManager.setupSolidRenderingTextureCombine(getTeamColor(entity));
			}

			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
			bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			if (renderOutlines) {
				GlStateManager.tearDownSolidRenderingTextureCombine();
				GlStateManager.disableColorMaterial();
			}

			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			int i = playerentity.getPrimaryHand() == HandSide.RIGHT ? 1 : -1;
			ItemStack itemstack = playerentity.getHeldItemMainhand();
			if (itemstack.getItem() != ModItems.ROD_OF_LYSSA) {
				i = -i;
			}

			float f3 = playerentity.getSwingProgress(partialTicks);
			float f4 = MathHelper.sin(MathHelper.sqrt(f3) * (float) Math.PI);
			float f5 = MathHelper.lerp(partialTicks, playerentity.prevRenderYawOffset, playerentity.renderYawOffset) * ((float) Math.PI / 180F);
			double d0 = MathHelper.sin(f5);
			double d1 = MathHelper.cos(f5);
			double d2 = (double) i * 0.35D;
			double d4;
			double d5;
			double d6;
			double d7;
			if ((renderManager.options == null || renderManager.options.thirdPersonView <= 0) && playerentity == Minecraft.getInstance().player) {
				assert this.renderManager.options != null;
				double d8 = this.renderManager.options.fov;
				d8 = d8 / 100.0D;
				Vec3d vec3d = new Vec3d((double) i * -0.36D * d8, -0.045D * d8, 0.4D);
				vec3d = vec3d.rotatePitch(-MathHelper.lerp(partialTicks, playerentity.prevRotationPitch, playerentity.rotationPitch) * ((float) Math.PI / 180F));
				vec3d = vec3d.rotateYaw(-MathHelper.lerp(partialTicks, playerentity.prevRotationYaw, playerentity.rotationYaw) * ((float) Math.PI / 180F));
				vec3d = vec3d.rotateYaw(f4 * 0.5F);
				vec3d = vec3d.rotatePitch(-f4 * 0.7F);
				d4 = MathHelper.lerp(partialTicks, playerentity.prevPosX, playerentity.posX) + vec3d.x;
				d5 = MathHelper.lerp(partialTicks, playerentity.prevPosY, playerentity.posY) + vec3d.y;
				d6 = MathHelper.lerp(partialTicks, playerentity.prevPosZ, playerentity.posZ) + vec3d.z;
				d7 = playerentity.getEyeHeight();
			} else {
				d4 = MathHelper.lerp(partialTicks, playerentity.prevPosX, playerentity.posX) - d1 * d2 - d0 * 0.8D;
				d5 = playerentity.prevPosY + (double) playerentity.getEyeHeight() + (playerentity.posY - playerentity.prevPosY) * (double) partialTicks - 0.45D;
				d6 = MathHelper.lerp(partialTicks, playerentity.prevPosZ, playerentity.posZ) - d0 * d2 + d1 * 0.8D;
				d7 = playerentity.shouldRenderSneaking() ? -0.1875D : 0.0D;
			}

			double d13 = MathHelper.lerp(partialTicks, entity.prevPosX, entity.posX);
			double d14 = MathHelper.lerp(partialTicks, entity.prevPosY, entity.posY) + 0.25D;
			double d9 = MathHelper.lerp(partialTicks, entity.prevPosZ, entity.posZ);
			double d10 = (float) (d4 - d13);
			double d11 = (double) ((float) (d5 - d14)) + d7;
			double d12 = (float) (d6 - d9);
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

			for (int k = 0; k <= 16; ++k) {
				float f6 = (float) k / 16.0F;
				bufferbuilder.pos(x + d10 * (double) f6, y + d11 * (double) (f6 * f6 + f6) * 0.5D + 0.25D, z + d12 * (double) f6).color(0, 0, 0, 255).endVertex();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(LyssaBobberEntity entity) {
		return FISH_PARTICLES;
	}
}
