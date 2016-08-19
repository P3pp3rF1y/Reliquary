package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import xreliquary.api.client.IPedestalItemRenderer;
import xreliquary.blocks.tile.TileEntityPedestal;

public class RenderPedestalFishHook implements IPedestalItemRenderer {

	private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");
	private static RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

	@Override
	public void doRender(TileEntityPedestalRenderer tesr, TileEntityPedestal te, ItemStack stack, double x, double y, double z, float partialTicks, int destroyStage) {
		Object itemData = te.getItemData(0);

		if(itemData == null || !(itemData instanceof HookRenderingData))
			return;

		HookRenderingData data = (HookRenderingData) itemData;

		double translateX = data.hookX - te.getPos().getX() + x;
		double translateY = data.hookY - te.getPos().getY() + y;
		double translateZ = data.hookZ - te.getPos().getZ() + z;

		GlStateManager.pushMatrix();
		GlStateManager.translate(translateX, translateY, translateZ);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		renderManager.renderEngine.bindTexture(FISH_PARTICLES);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();

		GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		vertexbuffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertexbuffer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertexbuffer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
		vertexbuffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
		tessellator.draw();

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		//TODO fix/calculate yawoffset
		double d4;
		double d5;
		double d6;

		d4 = te.getPos().getX() + 0.5D;
		d5 = te.getPos().getY() + 0.7D;
		d6 = te.getPos().getZ() + 0.5D;

		double d13 = data.hookX;
		double d8 = data.hookY + 0.25D;
		double d9 = data.hookZ;
		double d10 = (double) ((float) (d4 - d13));
		double d11 = (double) ((float) (d5 - d8));
		double d12 = (double) ((float) (d6 - d9));
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		int l = 16;

		for(int i1 = 0; i1 <= 16; ++i1) {
			float f10 = (float) i1 / 16.0F;
			vertexbuffer.pos(translateX + d10 * (double) f10, translateY + d11 * (double) (f10 * f10 + f10) * 0.5D + 0.25D, translateZ + d12 * (double) f10).color(0, 0, 0, 255).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}

	public static class HookRenderingData {
		public final double hookX;
		public final double hookY;
		public final double hookZ;

		public HookRenderingData(double hookX, double hookY, double hookZ) {

			this.hookX = hookX;
			this.hookY = hookY;
			this.hookZ = hookZ;
		}
	}
}
