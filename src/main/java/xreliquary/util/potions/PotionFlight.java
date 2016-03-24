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
		super(/*new ResourceLocation(Reference.DOMAIN + "potions/flight"), //TODO see if this needs to get back*/ false, 0);
		this.setPotionName("flight");
	}

	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		super.renderInventoryEffect(x, y, effect, mc);

		mc.renderEngine.bindTexture(icon);

		GlStateManager.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 16, 16, 16, 16);
	}
}
