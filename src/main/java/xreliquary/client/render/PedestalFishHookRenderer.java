package xreliquary.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.api.client.IPedestalItemRenderer;
import xreliquary.blocks.tile.PedestalTileEntity;

public class PedestalFishHookRenderer implements IPedestalItemRenderer {
	private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");
	private static EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();

	@Override
	public void doRender(PedestalRenderer tesr, PedestalTileEntity te,
			ItemStack stack, double x, double y, double z, float partialTicks, int destroyStage) {
		te.getItemData().ifPresent(itemData -> renderHook(te, x, y, z, itemData));
	}

	private void renderHook(PedestalTileEntity te, double x, double y, double z, Object itemData) {
		if (!(itemData instanceof HookRenderingData)) {
			return;
		}

		HookRenderingData hookData = (HookRenderingData) itemData;
		double translateX = hookData.hookX - te.getPos().getX() + x;
		double translateY = hookData.hookY - te.getPos().getY() + y;
		double translateZ = hookData.hookZ - te.getPos().getZ() + z;

		GlStateManager.pushMatrix();
		GlStateManager.translated(translateX, translateY, translateZ);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(0.5F, 0.5F, 0.5F);
		renderManager.textureManager.bindTexture(FISH_PARTICLES);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.rotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float) (renderManager.options.thirdPersonView == 2 ? -1 : 1) * -renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
		bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
		bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
		bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
		tessellator.draw();

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		double d4 = te.getPos().getX() + 0.5D;
		double d5 = te.getPos().getY() + 0.7D;
		double d6 = te.getPos().getZ() + 0.5D;

		double d13 = hookData.hookX;
		double d8 = hookData.hookY + 0.25D;
		double d9 = hookData.hookZ;

		double d10 = (float) (d4 - d13);
		double d11 = (float) (d5 - d8);
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
	}

	public static class HookRenderingData {
		final double hookX;
		final double hookY;
		final double hookZ;

		public HookRenderingData(double hookX, double hookY, double hookZ) {

			this.hookX = hookX;
			this.hookY = hookY;
			this.hookZ = hookZ;
		}
	}
}
