package xreliquary.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import xreliquary.init.ModItems;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class MobCharmBeltLayerRenderer implements LayerRenderer<EntityPlayer> {

	private static ModelBiped model;
	private static final ResourceLocation ON_BODY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/models/armor/mob_charm_belt.png");

	@Override
	public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if(Loader.isModLoaded(Compatibility.MOD_ID.BAUBLES)) {
/* TODO readd when Baubles are updated
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

			for(int i = 0; i < baubles.getSlots(); i++) {
				ItemStack bauble = baubles.getStackInSlot(i);
				if(bauble.isEmpty() || bauble.getItem() != ModItems.mobCharmBelt)
					continue;

				GlStateManager.pushMatrix();

				Minecraft.getMinecraft().renderEngine.bindTexture(ON_BODY_TEXTURE);
				if (player.isSneaking()) {
					GlStateManager.translate(0F, 0.2F, 0F);
					GlStateManager.rotate(90F / (float) Math.PI, 1.0F, 0.0F, 0.0F);
				}

				GlStateManager.translate(0F, 0.2F, 0F);

				float s = 1.05F / 16F;
				GlStateManager.scale(s, s, s);
				if(model == null)
					model = new ModelBiped();

				model.bipedBody.render(1F);
				GlStateManager.popMatrix();
			}
*/
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
