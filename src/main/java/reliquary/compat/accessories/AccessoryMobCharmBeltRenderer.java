package reliquary.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;

public class AccessoryMobCharmBeltRenderer implements AccessoryRenderer {
	public static final ModelLayerLocation MOB_CHARM_BELT_LAYER = new ModelLayerLocation(Reliquary.getRL("mob_charm_belt"), "main");
	private static final ResourceLocation ON_BODY_TEXTURE = Reliquary.getRL("textures/models/armor/mob_charm_belt.png");
	private final HumanoidModel<Player> beltModel;

	public AccessoryMobCharmBeltRenderer() {
		EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		beltModel = new HumanoidModel<>(entityModels.bakeLayer(MOB_CHARM_BELT_LAYER));
	}

	@Override
	public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack poseStack, EntityModel<M> model, MultiBufferSource multiBufferSource, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (model instanceof HumanoidModel<M> humanoidModel) {
			beltModel.body.copyFrom(humanoidModel.body);
		}
		VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(multiBufferSource, RenderType.entityCutoutNoCull(ON_BODY_TEXTURE), false, false);
		beltModel.setAllVisible(false);
		beltModel.body.visible = true;
		beltModel.body.render(poseStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY);
	}
}
