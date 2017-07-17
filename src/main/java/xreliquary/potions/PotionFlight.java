package xreliquary.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import xreliquary.init.ModPotions;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class PotionFlight extends Potion {
	private static final ResourceLocation ICON = new ResourceLocation(Reference.MOD_ID, "textures/gui/flight_effect.png");

	public PotionFlight() {
		super(false, 0);
		this.setPotionName("xreliquary.potion.flight");
		this.setIconIndex(0, 0);
		this.setRegistryName(Reference.MOD_ID, "flight_potion");
	}

	@Override
	public boolean hasStatusIcon() {
		return false;
	}

	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		render(mc, x + 3, y + 4);
	}

	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		render(mc, x + 6, y + 7);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entityLivingBase, int p_76394_2_) {
		if (entityLivingBase.world.isRemote || !(entityLivingBase instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLivingBase;

		if (!player.capabilities.allowFlying) {
			player.capabilities.allowFlying = true;
			((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		}
		player.fallDistance = 0;
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBase, @Nonnull AbstractAttributeMap attributeMap, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBase, attributeMap, amplifier);

		if (!(entityLivingBase instanceof EntityPlayer))
			return;

		EntityPlayer player = (EntityPlayer) entityLivingBase;

		if (player.getActivePotionEffect(ModPotions.potionFlight) != null)
			return;

		if (!player.capabilities.isCreativeMode) {
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
			((EntityPlayerMP) player).connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		}
	}

	private void render(Minecraft mc, int x, int y) {
		mc.renderEngine.bindTexture(ICON);

		GlStateManager.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
	}
}
