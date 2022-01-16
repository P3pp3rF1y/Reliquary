package xreliquary.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import xreliquary.reference.Reference;

import static xreliquary.compat.curios.CuriosCompat.MOB_CHARM_BELT_LAYER;

public class MobCharmBeltRenderer implements ICurioRenderer {
	private static final ResourceLocation ON_BODY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/models/armor/mob_charm_belt.png");
	private final HumanoidModel<Player> model;

	public MobCharmBeltRenderer() {
		EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();
		model = new HumanoidModel<>(entityModels.bakeLayer(MOB_CHARM_BELT_LAYER));
	}

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!stack.isEmpty()) {

			matrixStack.pushPose();

			if (slotContext.entity().isCrouching()) {
				matrixStack.translate(0D, 0.2D, 0D);
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F / (float) Math.PI));
			}

			matrixStack.translate(0D, 0.2D, 0D);

			VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(buffer, RenderType.entityCutoutNoCull(ON_BODY_TEXTURE), false, false);
			model.body.render(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY);
			matrixStack.popPose();
		}
	}
}
