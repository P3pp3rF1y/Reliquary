package xreliquary.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.lib.Reference;

public class RenderThrown extends Render {
	private int itemIconIndex;

	public RenderThrown(int par1) {
		itemIconIndex = par1;
	}

	@Override
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)par2, (float)par4, (float)par6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		Tessellator var10 = Tessellator.instance;
		float var3 = (itemIconIndex % 16 * 16 + 0) / 256.0F;
		float var4 = (itemIconIndex % 16 * 16 + 16) / 256.0F;
		float var5 = (itemIconIndex / 16 * 16 + 0) / 256.0F;
		float var6 = (itemIconIndex / 16 * 16 + 16) / 256.0F;
		float var7 = 1.0F;
		float var8 = 0.5F;
		float var9 = 0.25F;
		GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		var10.startDrawingQuads();
		var10.setNormal(0.0F, 1.0F, 0.0F);
		var10.addVertexWithUV(0.0F - var8, 0.0F - var9, 0.0D, var3, var6);
		var10.addVertexWithUV(var7 - var8, 0.0F - var9, 0.0D, var4, var6);
		var10.addVertexWithUV(var7 - var8, var7 - var9, 0.0D, var4, var5);
		var10.addVertexWithUV(0.0F - var8, var7 - var9, 0.0D, var3, var5);
		var10.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity) {
		return new ResourceLocation(Reference.MOD_ID, Reference.ART_PATH_ENTITIES + Reference.THROWN_ITEM_SPRITES);
	}
}
