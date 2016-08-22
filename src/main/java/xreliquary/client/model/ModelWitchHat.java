package xreliquary.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelWitchHat extends ModelBiped {

	public static ModelWitchHat self = new ModelWitchHat();

	public static ModelRenderer witchHat;

	public ModelWitchHat() {
		super();
		witchHat = (new ModelRenderer(this)).setTextureSize(64, 128);
		witchHat.setRotationPoint(0.0f, 0.0f, 0.0f);
		witchHat.setTextureOffset(0, 64).addBox(-5.0F, -10F, -5.0F, 10, 2, 10, 0.5f);
		ModelRenderer modelrenderer = (new ModelRenderer(this)).setTextureSize(64, 128);
		modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelrenderer.setTextureOffset(0, 76).addBox(-5.25F, -10F, -5.25F, 7, 4, 7);
		modelrenderer.rotateAngleX = -0.05235988F;
		modelrenderer.rotateAngleZ = 0.02617994F;
		witchHat.addChild(modelrenderer);
	}

	@Override
	public void render(Entity par1Entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, par1Entity);
		witchHat.rotateAngleY = this.bipedHead.rotateAngleY;
		witchHat.rotateAngleX = this.bipedHead.rotateAngleX;

		if(this.isSneak)
			witchHat.rotationPointY = 1.0F;
		else
			witchHat.rotationPointY = 0.0F;

		GlStateManager.pushMatrix();
		witchHat.render(f5);
		GlStateManager.popMatrix();
	}
}
