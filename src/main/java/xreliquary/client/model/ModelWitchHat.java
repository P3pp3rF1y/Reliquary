package xreliquary.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelWitchHat extends ModelBiped {

	public static ModelWitchHat self = new ModelWitchHat();

	public static ModelRenderer witchHat;

	public ModelWitchHat() {
		super();
		this.witchHat = (new ModelRenderer(this)).setTextureSize(64, 128);
		this.witchHat.setRotationPoint(0.0f, 0.0f, 0.0f);
		this.witchHat.setTextureOffset(0, 64).addBox(-5.0F, -10F, -5.0F, 10, 2, 10, 0.5f);
		ModelRenderer modelrenderer = (new ModelRenderer(this)).setTextureSize(64, 128);
		modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelrenderer.setTextureOffset(0, 76).addBox(-5.25F, -10F, -5.25F, 7, 4, 7);
		modelrenderer.rotateAngleX = -0.05235988F;
		modelrenderer.rotateAngleZ = 0.02617994F;
		this.witchHat.addChild(modelrenderer);
	}

	@Override
	public void render(Entity par1Entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, par1Entity);
		this.witchHat.rotateAngleY = this.bipedHead.rotateAngleY;
		this.witchHat.rotateAngleX = this.bipedHead.rotateAngleX;

		if(this.isSneak)
			this.witchHat.rotationPointY = 1.0F;
		else
			this.witchHat.rotationPointY = 0.0F;

		GL11.glPushMatrix();
		this.witchHat.render(f5);
		GL11.glPopMatrix();
	}
}
