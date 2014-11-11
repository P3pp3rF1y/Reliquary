package xreliquary.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xreliquary.entities.potion.EntityThrownXRPotion;
import xreliquary.items.ItemXRPotion;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.potions.PotionEssence;

public class RenderThrownPotion extends Render {
    private static ResourceLocation potionTexture = new ResourceLocation(Reference.MOD_ID + ":textures/items/" + Names.potion_splash + ".png");
    private static ResourceLocation potionOverlay = new ResourceLocation(Reference.MOD_ID + ":textures/items/" + Names.potion_splash_overlay + ".png");
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        IIcon iicon = ItemXRPotion.iconSplash;

        if (iicon != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2, (float)par4, (float)par6);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            //this.bindEntityTexture(par1Entity);
            this.bindTexture(potionTexture);
            Tessellator tessellator = Tessellator.instance;

            if (iicon == ItemPotion.func_94589_d("bottle_splash"))
            {
                ItemStack potionStack = ((EntityThrownXRPotion) par1Entity).potionItemStack;
                int color = PotionHelper.calcPotionLiquidColor(new PotionEssence(potionStack.getTagCompound()).getEffects());
                float f2 = (float)(color >> 16 & 255) / 255.0F;
                float f3 = (float)(color >> 8 & 255) / 255.0F;
                float f4 = (float)(color & 255) / 255.0F;
                GL11.glColor3f(f2, f3, f4);
                GL11.glPushMatrix();
                this.bindTexture(potionOverlay);
                this.func_77026_a(tessellator, ItemXRPotion.iconSplashOverlay);
                GL11.glPopMatrix();
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }

            this.func_77026_a(tessellator, iicon);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return potionTexture;
    }

    private void func_77026_a(Tessellator p_77026_1_, IIcon p_77026_2_)
    {
        float f = p_77026_2_.getMinU();
        float f1 = p_77026_2_.getMaxU();
        float f2 = p_77026_2_.getMinV();
        float f3 = p_77026_2_.getMaxV();
        float f4 = 1.0F;
        float f5 = 0.5F;
        float f6 = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        p_77026_1_.startDrawingQuads();
        p_77026_1_.setNormal(0.0F, 1.0F, 0.0F);
        p_77026_1_.addVertexWithUV((double)(0.0F - f5), (double)(0.0F - f6), 0.0D, (double)f, (double)f3);
        p_77026_1_.addVertexWithUV((double)(f4 - f5), (double)(0.0F - f6), 0.0D, (double)f1, (double)f3);
        p_77026_1_.addVertexWithUV((double)(f4 - f5), (double)(f4 - f6), 0.0D, (double)f1, (double)f2);
        p_77026_1_.addVertexWithUV((double)(0.0F - f5), (double)(f4 - f6), 0.0D, (double)f, (double)f2);
        p_77026_1_.draw();
    }
}
