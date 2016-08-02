package xreliquary.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public class GuiMobCharmBelt extends GuiBase {
	private static final ResourceLocation BELT_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/mob_charm_belt.png");
	private static int WIDTH = 175;
	private static int HEIGHT = 221;

	private ItemStack belt;

	public GuiMobCharmBelt(ContainerMobCharmBelt container) {
		super(container);
		this.belt = container.getBelt();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindTexture(this.BELT_TEX);
		int i = (this.width - WIDTH) / 2;
		int j = (this.height - HEIGHT) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, WIDTH, HEIGHT);

		updateMobCharmSlots(i, j);
	}

	private void updateMobCharmSlots(int xLeft, int yTop) {
		int slots = ModItems.mobCharmBelt.getCharmCount(belt);
		slots = Math.min(slots, Reference.MOB_CHARM.COUNT_TYPES);

		double radius = 44.0;
		int centerX = xLeft + 88;
		int centerY = yTop + 67;

		double increment = 2d * Math.PI / ((double) (slots + 1));
		double start = Math.PI / 2d;

		for(int i = 1; i <= slots; i++) {
			double angle = start + (((double) i) * increment);

			int offsetX = (int) Math.round(Math.cos(angle) * radius);
			int offsetY = (int) Math.round(Math.sin(angle) * radius);

			int x = centerX - offsetX - 8;
			int y = centerY - offsetY - 8;

			this.drawTexturedModalRect(x, y, 176, 0, 16, 16);
			this.inventorySlots.inventorySlots.get(i - 1).xDisplayPosition = x - xLeft;
			this.inventorySlots.inventorySlots.get(i - 1).yDisplayPosition = y - yTop - 27;
		}
		this.inventorySlots.inventorySlots.get(slots).xDisplayPosition = 80;// xLeft + 80;
		this.inventorySlots.inventorySlots.get(slots).yDisplayPosition = -12; // yTop + 35;
	}
}
