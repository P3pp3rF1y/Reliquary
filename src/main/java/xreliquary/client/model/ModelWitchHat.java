package xreliquary.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelWitchHat extends ModelBiped {

    public static ModelWitchHat self = new ModelWitchHat();


    public ModelWitchHat() {
        super();
        this.bipedHeadwear = (new ModelRenderer(this)).setTextureSize(64, 128);
        this.bipedHeadwear.setRotationPoint(-5.0F, -10.03125F, -5.0F);
        this.bipedHeadwear.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10, 0.5f);
        ModelRenderer modelrenderer = (new ModelRenderer(this)).setTextureSize(64, 128);
        modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
        modelrenderer.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
        modelrenderer.rotateAngleX = -0.05235988F;
        modelrenderer.rotateAngleZ = 0.02617994F;
        this.bipedHeadwear.addChild(modelrenderer);
    }

    @Override
    public void render(Entity par1Entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, par1Entity);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -5.0F * f5, 0.26F);
        GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
        this.bipedHeadwear.render(f5);
        GL11.glPopMatrix();
    }
}
