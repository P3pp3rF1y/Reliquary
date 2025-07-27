package reliquary.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import reliquary.Reliquary;
import reliquary.common.gui.MobCharmBeltMenu;
import reliquary.init.ModItems;
import reliquary.item.MobCharmRegistry;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MobCharmBeltScreen extends BaseScreen<MobCharmBeltMenu> {
	private static final ResourceLocation BELT_TEX = Reliquary.getRL("textures/gui/mob_charm_belt.png");
	private static final ResourceLocation BELT_ITEM_TEX = Reliquary.getRL("textures/item/mob_charm_belt.png");
	private static final int WIDTH = 175;
	private static final int HEIGHT = 221;

	private final ItemStack belt;

	public MobCharmBeltScreen(MobCharmBeltMenu container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		belt = container.getBelt();
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		int i = leftPos;
		int j = topPos;

		guiGraphics.blit(BELT_TEX, i, j - 27, 0, 0, WIDTH, HEIGHT);

		int centerX = i + 88;
		int centerY = j + 40;

		updateMobCharmSlots(guiGraphics, centerX, centerY);

		GlStateManager._enableBlend();
		guiGraphics.blit(BELT_ITEM_TEX, centerX - 26, centerY - 26, 0, 0, 48, 48, 48, 48);
		GlStateManager._disableBlend();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
		//noop - to prevent name of inventory being rendered
	}

	private void updateMobCharmSlots(GuiGraphics guiGraphics, int centerX, int centerY) {
		int slots = ModItems.MOB_CHARM_BELT.get().getCharmCount(belt);
		slots = Math.min(slots, MobCharmRegistry.getRegisteredNames().size());

		double radius = 44.0;

		double increment = 2d * Math.PI / (slots + 1);
		double start = Math.PI / 2d;

		for (int i = 1; i <= slots; i++) {
			double angle = start + (i * increment);

			int offsetX = (int) Math.round(Math.cos(angle) * radius);
			int offsetY = (int) Math.round(Math.sin(angle) * radius);

			int x = centerX - offsetX - 8;
			int y = centerY - offsetY - 8;

			RenderSystem.enableBlend();

			guiGraphics.blit(BELT_TEX, x, y, 176, 0, 16, 16);

			RenderSystem.disableBlend();

			menu.slots.get(i - 1).x = x - centerX + 88;
			menu.slots.get(i - 1).y = y - centerY + 40;
		}
		menu.slots.get(slots).x = 80;
		menu.slots.get(slots).y = -12;

		for (int i = slots + 1; i < MobCharmRegistry.getRegisteredNames().size() + 1; i++) {
			menu.slots.get(i).x = -999;
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}
}
