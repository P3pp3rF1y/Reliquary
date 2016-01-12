package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.potions.PotionEssence;

public class RenderThrownPotion extends Render<Entity> {
    private static ResourceLocation potionTexture = new ResourceLocation(Reference.MOD_ID + ":textures/items/" + Names.potion_splash + ".png");
    private static ResourceLocation potionOverlay = new ResourceLocation(Reference.MOD_ID + ":textures/items/" + Names.potion_splash_overlay + ".png");

    public RenderThrownPotion( RenderManager renderManager )
    {
        super( renderManager );
    }

    @Override
    public void doRender(Entity potionEntity, double par2, double par4, double par6, float par8, float par9) {
//TODO: review if this is needed for anything
//        IIcon iicon = ItemXRPotion.iconSplash;
//
//        if (iicon != null)
//        {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        Tessellator tessellator = Tessellator.getInstance();
        PotionEssence essence = ((EntityThrownXRPotion) potionEntity).essence;
        int color =  ((EntityThrownXRPotion) potionEntity).getEntityColor();
        if (essence != null && essence.getEffects().size() > 0)
            color = PotionHelper.calcPotionLiquidColor(essence.getEffects());
        float f2 = (float)(color >> 16 & 255) / 255.0F;
        float f3 = (float)(color >> 8 & 255) / 255.0F;
        float f4 = (float)(color & 255) / 255.0F;
        GL11.glColor3f(f2, f3, f4);
        GL11.glPushMatrix();
        //this.bindTexture(potionOverlay);
        Minecraft.getMinecraft().getTextureManager().bindTexture(potionOverlay);
        this.func_77026_a(tessellator);
        GL11.glPopMatrix();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);


        Minecraft.getMinecraft().getTextureManager().bindTexture(potionTexture);
        this.func_77026_a(tessellator);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return potionTexture;
    }

    private void func_77026_a(Tessellator tessellator)
    {
        float f = 0F;
        float f1 = 1F;
        float f2 = 0F;
        float f3 = 1F;
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin( 7, DefaultVertexFormats.POSITION_TEX_NORMAL );
        worldrenderer.pos((double)(0.0F - f5), (double)(0.0F - f6), 0.0D).tex((double)f, (double)f3).normal(0.0F, 1.0F, 0.0F);
        worldrenderer.pos( (double) ( f4 - f5 ), (double) ( 0.0F - f6 ), 0.0D).tex( (double) f1, (double) f3 ).normal( 0.0F, 1.0F, 0.0F );
        worldrenderer.pos( (double) ( f4 - f5 ), (double) ( f4 - f6 ), 0.0D ).tex( (double) f1, (double) f2 ).normal( 0.0F, 1.0F, 0.0F );
        worldrenderer.pos((double)(0.0F - f5), (double) ( f4 - f6 ), 0.0D ).tex((double)f, (double)f2).normal(0.0F, 1.0F, 0.0F);
        tessellator.draw();
    }
}
