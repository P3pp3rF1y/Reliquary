package xreliquary.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;

public class GuiMobCharmBelt extends GuiBase {
	private static final ResourceLocation BELT_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/mob_charm_belt.png");

	public GuiMobCharmBelt(Container container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture(this.BELT_TEX);
		int i = (this.width - 175) / 2;
		int j = (this.height - 221) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, 175, 221);

	}
}
