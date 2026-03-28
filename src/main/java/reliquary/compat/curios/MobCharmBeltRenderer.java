package reliquary.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;
import reliquary.client.model.MobCharmBeltModel;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class MobCharmBeltRenderer implements ICurioRenderer {
	private static final Identifier ON_BODY_TEXTURE = Reliquary.getIdentifier("textures/entity/equipment/humanoid/mob_charm_belt.png");
	private final HumanoidModel<HumanoidRenderState> model;

	public MobCharmBeltRenderer() {
		EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		model = new HumanoidModel<>(entityModels.bakeLayer(MobCharmBeltModel.MOB_CHARM_BELT_LAYER));
	}

	@Override
	public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int packedLight, S renderState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context, float yRotation, float xRotation) {
		if (!stack.isEmpty()) {
			if (renderState instanceof HumanoidRenderState humanoidRenderState) {
				model.setupAnim(humanoidRenderState);
			}
			VertexConsumer vertexBuilder = ItemFeatureRenderer.getFoilBuffer(renderTypeBuffer, RenderTypes.armorCutoutNoCull(ON_BODY_TEXTURE), false, stack.hasFoil());
			model.head.visible = false;
			model.hat.visible = false;
			model.body.visible = false;
			model.rightArm.visible = false;
			model.leftArm.visible = false;
			model.rightLeg.visible = false;
			model.leftLeg.visible = false;
			model.body.visible = true;
			model.body.render(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
		}
	}
}
