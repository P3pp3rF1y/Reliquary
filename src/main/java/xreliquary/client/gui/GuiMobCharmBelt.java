package xreliquary.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;

public class GuiMobCharmBelt extends GuiBase {
	private static final ResourceLocation BELT_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/mob_charm_belt.png");
	private static int WIDTH = 175;
	private static int HEIGHT = 221;

	public GuiMobCharmBelt(Container container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture(this.BELT_TEX);
		int i = (this.width - WIDTH) / 2;
		int j = (this.height - HEIGHT) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, WIDTH, HEIGHT);

		drawMobCharmSlots(i, j);
	}

	private void drawMobCharmSlots(int xLeft, int yTop) {
		int slots = 13;
		double radius = 44.0;
		int centerX = xLeft + 88;
		int centerY = yTop + 67;

		double increment = 2d * Math.PI / ((double) (slots + 1));
		double start = Math.PI / 2d;

		for (int i=1; i<=slots; i++) {
			double angle = start + (((double) i) * increment);

			int offsetX = (int) Math.round(Math.cos(angle) * radius);
			int offsetY = (int) Math.round(Math.sin(angle) * radius);

			int x = centerX - offsetX - 8;
			int y = centerY - offsetY - 8;

			this.drawTexturedModalRect(x, y, 176, 0, 16, 16);
		}
	}
}
