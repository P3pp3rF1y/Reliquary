/* TODO  add baubles replacement compatibility
package xreliquary.client.render;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;

public class MobCharmBeltLayerRenderer extends LayerRenderer<PlayerEntity, BipedModel<PlayerEntity>> {
	private BipedModel<PlayerEntity> model = new BipedModel<>();
	private static final ResourceLocation ON_BODY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/models/armor/mob_charm_belt.png");

	public MobCharmBeltLayerRenderer(IEntityRenderer<PlayerEntity, BipedModel<PlayerEntity>> p_i50926_1_) {
		super(p_i50926_1_);
	}

	@Override
	public void render(PlayerEntity player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if(ModList.get().isLoaded(Compatibility.MOD_ID.BAUBLES)) {
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < baubles.getSlots(); i++) {
				ItemStack bauble = baubles.getStackInSlot(i);
				if(bauble.isEmpty() || bauble.getItem() != ModItems.MOB_CHARM_BELT)
					continue;

				GlStateManager.pushMatrix();

				Minecraft.getInstance().textureManager.bindTexture(ON_BODY_TEXTURE);
				if (player.isSneaking()) {
					GlStateManager.translatef(0F, 0.2F, 0F);
					GlStateManager.rotate(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
				}

				GlStateManager.translatef(0F, 0.2F, 0F);

				float s = 1.05F / 16F;
				GlStateManager.scalef(s, s, s);
				model.bipedBody.render(1F);
				GlStateManager.popMatrix();
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
*/
