package xreliquary.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public class ModelWitchHat extends ModelBiped {

	public static ModelWitchHat self = new ModelWitchHat();

	private static ModelRenderer witchHat;

	@SuppressWarnings("WeakerAccess")
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
	public void render(@Nullable Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		copyModelAngles(this.bipedHead, witchHat);
		GlStateManager.pushMatrix();

		if (this.isSneak) {
			GlStateManager.translate(0.0F, 0.2F, 0.0F);
		}

		witchHat.render(scale);
		GlStateManager.popMatrix();
	}
}
