package xreliquary.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;

public class WitchHatModel extends BipedModel<LivingEntity> {
	public static final WitchHatModel SELF = new WitchHatModel();

	private RendererModel witchHat;

	private WitchHatModel() {
		super();
		witchHat = (new RendererModel(this)).setTextureSize(64, 128);
		witchHat.setRotationPoint(0.0f, 0.0f, 0.0f);
		witchHat.setTextureOffset(0, 64).addBox(-5.0F, -10F, -5.0F, 10, 2, 10, 0.5f);
		RendererModel modelrenderer = (new RendererModel(this)).setTextureSize(64, 128);
		modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
		modelrenderer.setTextureOffset(0, 76).addBox(-5.25F, -10F, -5.25F, 7, 4, 7);
		modelrenderer.rotateAngleX = -0.05235988F;
		modelrenderer.rotateAngleZ = 0.02617994F;
		witchHat.addChild(modelrenderer);
	}

	@Override
	public void render(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		witchHat.copyModelAngles(bipedHead);
		GlStateManager.pushMatrix();

		if (isSneak) {
			GlStateManager.translatef(0.0F, 0.2F, 0.0F);
		}

		witchHat.render(scale);
		GlStateManager.popMatrix();
	}
}
