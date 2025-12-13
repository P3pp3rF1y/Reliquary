package reliquary.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;
import reliquary.client.model.MobCharmBeltModel;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class MobCharmBeltRenderer implements ICurioRenderer {
	private static final ResourceLocation ON_BODY_TEXTURE = Reliquary.getRL("textures/models/armor/mob_charm_belt.png");
	private final HumanoidModel<Player> model;

	public MobCharmBeltRenderer() {
		EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		model = new HumanoidModel<>(entityModels.bakeLayer(MobCharmBeltModel.MOB_CHARM_BELT_LAYER));
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!stack.isEmpty()) {
			model.crouching = slotContext.entity().isCrouching();
			if (slotContext.entity() instanceof Player player) {
				model.setupAnim(player, limbSwing,limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			}
			VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(buffer, RenderType.entityCutoutNoCull(ON_BODY_TEXTURE), false, false);
			model.body.render(poseStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
		}
	}
}
