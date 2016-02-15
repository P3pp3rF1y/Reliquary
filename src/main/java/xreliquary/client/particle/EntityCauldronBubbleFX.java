package xreliquary.client.particle;


import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

/**
 * Created by Xeno on 11/9/2014.
 */
@SideOnly(Side.CLIENT)
public class EntityCauldronBubbleFX extends EntityFX {
    private static ResourceLocation bubbleTexture = new ResourceLocation(Reference.MOD_ID + ":textures/particles/" + Names.cauldron_bubble + ".png");

    private TextureManager theRenderEngine;

    public EntityCauldronBubbleFX(TextureManager renderEngine, World world, double x, double y, double z, double xMot, double yMot, double zMot, float red, float green, float blue) {
        super(world, x, y, z, xMot, yMot, zMot);
        this.setSize(0.02F, 0.02F);
        theRenderEngine = renderEngine;
        this.particleScale = 0.5F + (worldObj.rand.nextFloat() - 0.5F) * 0.4F;
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
    public void renderParticle(WorldRenderer worldRenderer, Entity entity, float subTick, float rotX, float rotZ, float rotY_Z, float rotX_Y, float rotX_Z) {
        //TODO: look into whether this needs to be changed to this.theRenderEngine.bindTexture as it is in EntityLargeExplodeFX
        this.theRenderEngine.bindTexture(bubbleTexture);
        float uMin = (float)this.particleTextureIndexX / 1F; // 1 is number of textures on the sheet (X)
        float uMax = uMin + 1F; // always 1 / number of textures X
        float vMin = (float)this.particleTextureIndexY / 4F; // same, on Y (4)
        float vMax = vMin + 1F / 4F;

        float scale = 0.1F * this.particleScale * (1.0F + (float)this.particleAge / 20F);
        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * subTick - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * subTick - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * subTick - interpPosZ);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();

        int i = this.getBrightnessForRender(subTick);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);

        //TODO: verify that the vertex format is correct for this particle
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x - rotX * scale - rotX_Y * scale, y - rotZ * scale, z - rotY_Z * scale - rotX_Z * scale).tex(uMax, vMax).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).endVertex(); //.lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRenderer.pos(x - rotX * scale + rotX_Y * scale, y + rotZ * scale, z - rotY_Z * scale + rotX_Z * scale).tex(uMax, vMin).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).endVertex(); //.lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRenderer.pos(x + rotX * scale + rotX_Y * scale, y + rotZ * scale, z + rotY_Z * scale + rotX_Z * scale).tex(uMin, vMin).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).endVertex(); //.lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        worldRenderer.pos(x + rotX * scale - rotX_Y * scale, y - rotZ * scale, z + rotY_Z * scale - rotX_Z * scale).tex(uMin, vMax).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).endVertex(); //.lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
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
        if (ageFromDeath <= 6) {
            this.setParticleTextureIndex(4 - Math.max(ageFromDeath / 2, 1));
        }

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
    }
}