package xreliquary.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public abstract class MobCharmBeltLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	private BipedModel<PlayerEntity> model = new BipedModel<>();
	private static final ResourceLocation ON_BODY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/models/armor/mob_charm_belt.png");

	protected MobCharmBeltLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer) {
		super(entityRenderer);
	}

	@Override
	public void render(AbstractClientPlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		IItemHandler baubles = getBaublesHandler(player);

		for (int i = 0; i < baubles.getSlots(); i++) {
			ItemStack bauble = baubles.getStackInSlot(i);
			if (bauble.isEmpty() || bauble.getItem() != ModItems.MOB_CHARM_BELT) {
				continue;
			}

			GlStateManager.pushMatrix();

			Minecraft.getInstance().textureManager.bindTexture(ON_BODY_TEXTURE);
			if (player.isSneaking()) {
				GlStateManager.translatef(0F, 0.2F, 0F);
				GlStateManager.rotatef(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			}

			GlStateManager.translatef(0F, 0.2F, 0F);

			float s = 1.05F / 16F;
			GlStateManager.scalef(s, s, s);
			model.bipedBody.render(1F);
			GlStateManager.popMatrix();
			return;
		}
	}

	protected abstract IItemHandler getBaublesHandler(PlayerEntity player);

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
