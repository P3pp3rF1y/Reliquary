package xreliquary.util.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;

public class PotionFlight extends Potion {
	private ResourceLocation icon = new ResourceLocation(Reference.MOD_ID, "textures/gui/flight_effect.png");

	public PotionFlight() {
		super(false, 0);
		this.setPotionName("flight");
		this.setIconIndex(0,0);
		this.setRegistryName(Reference.MOD_ID, "flight_potion");
	}

	@Override
	public boolean hasStatusIcon() {
		return false;
	}

	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		super.renderInventoryEffect(x, y, effect, mc);

		mc.renderEngine.bindTexture(icon);

		GlStateManager.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 16, 16, 16, 16);
	}
}
