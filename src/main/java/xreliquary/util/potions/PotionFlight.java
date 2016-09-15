package xreliquary.util.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;

public class PotionFlight extends Potion {
	private static final ResourceLocation ICON = new ResourceLocation(Reference.MOD_ID, "textures/gui/flight_effect.png");

	public PotionFlight() {
		super(false, 0);
		this.setPotionName("flight");
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

	private void render(Minecraft mc, int x, int y) {
		mc.renderEngine.bindTexture(ICON);

		GlStateManager.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
	}
}
