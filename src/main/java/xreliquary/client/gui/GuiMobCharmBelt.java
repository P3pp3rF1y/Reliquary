package xreliquary.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

public class GuiMobCharmBelt extends GuiBase {
	private static final ResourceLocation BELT_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/mob_charm_belt.png");
	private static final ResourceLocation BELT_ITEM_TEX = new ResourceLocation(Reference.MOD_ID, "textures/items/mob_charm_belt.png");
	private static final int WIDTH = 175;
	private static final int HEIGHT = 221;

	private ItemStack belt;

	public GuiMobCharmBelt(ContainerMobCharmBelt container) {
		super(container);
		this.belt = container.getBelt();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int i = this.guiLeft;
		int j = this.guiTop;

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushAttrib();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		this.bindTexture(BELT_TEX);
		this.drawTexturedModalRect(i, j - 27, 0, 0, WIDTH, HEIGHT);

		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.popAttrib();

		int centerX = i + 88;
		int centerY = j + 40;

		updateMobCharmSlots(centerX, centerY);

		this.bindTexture(BELT_ITEM_TEX);
		drawModalRectWithCustomSizedTexture(centerX - 26, centerY - 26, 0, 0, 48, 48, 48, 48);
	}

	private void updateMobCharmSlots(int centerX, int centerY) {
		int slots = ModItems.mobCharmBelt.getCharmCount(belt);
		slots = Math.min(slots, Reference.MOB_CHARM.COUNT_TYPES);

		double radius = 44.0;

		double increment = 2d * Math.PI / ((double) (slots + 1));
		double start = Math.PI / 2d;

		for(int i = 1; i <= slots; i++) {
			double angle = start + (((double) i) * increment);

			int offsetX = (int) Math.round(Math.cos(angle) * radius);
			int offsetY = (int) Math.round(Math.sin(angle) * radius);

			int x = centerX - offsetX - 8;
			int y = centerY - offsetY - 8;

			GlStateManager.pushAttrib();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();

			this.drawTexturedModalRect(x, y, 176, 0, 16, 16);

			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.popAttrib();

			this.inventorySlots.inventorySlots.get(i - 1).xDisplayPosition = x - centerX + 88;
			this.inventorySlots.inventorySlots.get(i - 1).yDisplayPosition = y - centerY + 40;
		}
		this.inventorySlots.inventorySlots.get(slots).xDisplayPosition = 80;
		this.inventorySlots.inventorySlots.get(slots).yDisplayPosition = -12;

		for(int i = slots + 1; i < Reference.MOB_CHARM.COUNT_TYPES + 1; i++) {
			this.inventorySlots.inventorySlots.get(i).xDisplayPosition = -999;
		}
	}
}
