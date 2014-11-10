package xreliquary.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

/**
 * Created by Xeno on 11/9/2014.
 */
public class EntityCauldronBubbleFX extends EntityFX {
    private static ResourceLocation bubbleTexture = new ResourceLocation(Reference.MOD_ID + ":textures/particles/" + Names.cauldron_bubble + ".png");

    public EntityCauldronBubbleFX(World world, double x, double y, double z) {
        this(world, x, y, z, 0D, 0D, 0D, 1.0F, 1.0F, 1.0F);
    }

    public EntityCauldronBubbleFX(World world, double x, double y, double z, double xMot, double yMot, double zMot, float red, float green, float blue) {
        super(world, x, y, z, xMot, yMot, zMot);
        this.setSize(0.02F, 0.02F);
        this.particleScale = 0.5F;
        this.motionX = xMot;
        this.motionY = yMot;
        this.motionZ = zMot;
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.particleMaxAge = 20;
        this.particleAge = 0;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void renderParticle(Tessellator tess, float subTick, float rotX, float rotZ, float rotY_Z, float rotX_Y, float rotX_Z) {
        float uMin = (float)this.particleTextureIndexX / 1F; // 1 is number of textures on the sheet (X)
        float uMax = uMin + 1F; // always 1 / number of textures X
        float vMin = (float)this.particleTextureIndexY / 4F; // same, on Y (4)
        float vMax = vMin + 1F / 4F;

        float scale = 0.1F * this.particleScale;
        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * subTick - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * subTick - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * subTick - interpPosZ);


        Minecraft.getMinecraft().getTextureManager().bindTexture(bubbleTexture);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

        tess.startDrawingQuads();
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tess.addVertexWithUV(x - rotX * scale - rotX_Y * scale, y - rotZ * scale, z - rotY_Z * scale - rotX_Z * scale, uMax, vMax);
        tess.addVertexWithUV(x - rotX * scale + rotX_Y * scale, y + rotZ * scale, z - rotY_Z * scale + rotX_Z * scale, uMax, vMin);
        tess.addVertexWithUV(x + rotX * scale + rotX_Y * scale, y + rotZ * scale, z + rotY_Z * scale + rotX_Z * scale, uMin, vMin);
        tess.addVertexWithUV(x + rotX * scale - rotX_Y * scale, y - rotZ * scale, z + rotY_Z * scale - rotX_Z * scale, uMin, vMax);
        tess.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
    }

    @Override
    public void setParticleTextureIndex(int index)
    {
        this.particleTextureIndexY = index;
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
//        if (this.particleMaxAge >= 18)
//            this.moveEntity(0.02D, 0.0D, 0.02D);

        this.setAlphaF((float)this.particleAge / (float)this.particleMaxAge);

        // use setParticleTextureIndex based on the age of the particle
        int ageFromDeath = this.particleMaxAge - this.particleAge;
        if (ageFromDeath <= 9) {
            this.setParticleTextureIndex(4 - Math.max(ageFromDeath / 3, 1));
        }

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
    }
}