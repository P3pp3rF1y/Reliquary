package xreliquary.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class WitchHatModel extends BipedModel<LivingEntity> {
	public static final WitchHatModel SELF = new WitchHatModel();

	private ModelRenderer witchHat;

	private WitchHatModel() {
		super(1F);
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
	public void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		witchHat.copyModelAngles(bipedHead);
		matrixStack.push();
		witchHat.render(matrixStack, vertexBuilder, packedLight, packedOverlay, red, green, blue, alpha);
		matrixStack.pop();
	}
}
