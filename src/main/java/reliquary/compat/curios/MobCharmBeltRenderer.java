package reliquary.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
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
		model = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(MobCharmBeltModel.MOB_CHARM_BELT_LAYER));
	}

	@Override
	public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
			SubmitNodeCollector collector, int packedLight, S renderState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
			float yRotation, float xRotation) {
		if (renderState instanceof HumanoidRenderState humanoidRenderState) {
			model.setupAnim(humanoidRenderState);
		}
		model.body.visible = true;
		collector.submitModelPart(model.body, poseStack, RenderTypes.armorCutoutNoCull(ON_BODY_TEXTURE), packedLight, OverlayTexture.NO_OVERLAY, null);
		if (stack.hasFoil()) {
			collector.order(2).submitModelPart(model.body, poseStack, RenderTypes.trimmedArmorGlint(), packedLight, OverlayTexture.NO_OVERLAY, null);
		}
	}
}
