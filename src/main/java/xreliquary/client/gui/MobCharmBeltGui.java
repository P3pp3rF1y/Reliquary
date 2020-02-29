package xreliquary.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MobCharmBeltGui extends GuiBase<ContainerMobCharmBelt> {
	private static final ResourceLocation BELT_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/mob_charm_belt.png");
	private static final ResourceLocation BELT_ITEM_TEX = new ResourceLocation(Reference.MOD_ID, "textures/item/mob_charm_belt.png");
	private static final int WIDTH = 175;
	private static final int HEIGHT = 221;

	private ItemStack belt;

	public MobCharmBeltGui(ContainerMobCharmBelt container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
		belt = container.getBelt();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int i = guiLeft;
		int j = guiTop;

		bindTexture(BELT_TEX);
		GuiUtils.drawTexturedModalRect(i, j - 27, 0, 0, WIDTH, HEIGHT, 0);

		int centerX = i + 88;
		int centerY = j + 40;

		updateMobCharmSlots(centerX, centerY);

		bindTexture(BELT_ITEM_TEX);
		GlStateManager.enableBlend();
		blit(centerX - 26, centerY - 26, 0, 0, 48, 48, 48, 48);
		GlStateManager.disableBlend();
	}

	private void updateMobCharmSlots(int centerX, int centerY) {
		int slots = ModItems.MOB_CHARM_BELT.getCharmCount(belt);
		slots = Math.min(slots, Reference.MOB_CHARM.COUNT_TYPES);

		double radius = 44.0;

		double increment = 2d * Math.PI / ((double) (slots + 1));
		double start = Math.PI / 2d;

		for (int i = 1; i <= slots; i++) {
			double angle = start + (((double) i) * increment);

			int offsetX = (int) Math.round(Math.cos(angle) * radius);
			int offsetY = (int) Math.round(Math.sin(angle) * radius);

			int x = centerX - offsetX - 8;
			int y = centerY - offsetY - 8;

			GlStateManager.enableAlphaTest();
			GlStateManager.enableBlend();

			blit(x, y, 176, 0, 16, 16);

			GlStateManager.disableAlphaTest();
			GlStateManager.disableBlend();

			container.inventorySlots.get(i - 1).xPos = x - centerX + 88;
			container.inventorySlots.get(i - 1).yPos = y - centerY + 40;
		}
		container.inventorySlots.get(slots).xPos = 80;
		container.inventorySlots.get(slots).yPos = -12;

		for (int i = slots + 1; i < Reference.MOB_CHARM.COUNT_TYPES + 1; i++) {
			container.inventorySlots.get(i).xPos = -999;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
}
