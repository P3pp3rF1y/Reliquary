package xreliquary.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import xreliquary.common.gui.ContainerMobCharmBelt;
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmRegistry;
import xreliquary.reference.Reference;
import xreliquary.util.LogHelper;

import java.lang.reflect.Field;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MobCharmBeltGui extends GuiBase<ContainerMobCharmBelt> {
	private static final ResourceLocation BELT_TEX = new ResourceLocation(Reference.MOD_ID, "textures/gui/mob_charm_belt.png");
	private static final ResourceLocation BELT_ITEM_TEX = new ResourceLocation(Reference.MOD_ID, "textures/item/mob_charm_belt.png");
	private static final int WIDTH = 175;
	private static final int HEIGHT = 221;

	private final ItemStack belt;

	public MobCharmBeltGui(ContainerMobCharmBelt container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
		belt = container.getBelt();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		int i = guiLeft;
		int j = guiTop;

		bindTexture(BELT_TEX);
		GuiUtils.drawTexturedModalRect(matrixStack, i, j - 27, 0, 0, WIDTH, HEIGHT, 0);

		int centerX = i + 88;
		int centerY = j + 40;

		updateMobCharmSlots(matrixStack, centerX, centerY);

		bindTexture(BELT_ITEM_TEX);
		GlStateManager.enableBlend();
		blit(matrixStack, centerX - 26, centerY - 26, 0, 0, 48, 48, 48, 48);
		GlStateManager.disableBlend();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		//noop - to prevent name of inventory being rendered
	}

	private void updateMobCharmSlots(MatrixStack matrixStack, int centerX, int centerY) {
		int slots = ModItems.MOB_CHARM_BELT.get().getCharmCount(belt);
		slots = Math.min(slots, MobCharmRegistry.getRegisteredNames().size());

		double radius = 44.0;

		double increment = 2d * Math.PI / ((double) (slots + 1));
		double start = Math.PI / 2d;

		for (int i = 1; i <= slots; i++) {
			double angle = start + (((double) i) * increment);

			int offsetX = (int) Math.round(Math.cos(angle) * radius);
			int offsetY = (int) Math.round(Math.sin(angle) * radius);

			int x = centerX - offsetX - 8;
			int y = centerY - offsetY - 8;

			RenderSystem.enableAlphaTest();
			RenderSystem.enableBlend();

			blit(matrixStack, x, y, 176, 0, 16, 16);

			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();

			setSlotXPos(container.inventorySlots.get(i - 1),x - centerX + 88 );
			setSlotYPos(container.inventorySlots.get(i - 1), y - centerY + 40);
		}
		setSlotXPos(container.inventorySlots.get(slots), 80);
		setSlotYPos(container.inventorySlots.get(slots), -12);

		for (int i = slots + 1; i < MobCharmRegistry.getRegisteredNames().size() + 1; i++) {
			setSlotXPos(container.inventorySlots.get(i), -999);
		}
	}

	private static final Field SLOT_X_POS = ObfuscationReflectionHelper.findField(Slot.class, "field_75223_e");
	@SuppressWarnings("java:S3011") //the use of reflection to bypass field invisiblity is intentional and necessary here
	private static void setSlotXPos(Slot slot, int xPos) {
		try {
			SLOT_X_POS.set(slot, xPos);
		}
		catch (IllegalAccessException e) {
			LogHelper.error("Error setting xPos of Slot: ", e);
		}
	}

	private static final Field SLOT_Y_POS = ObfuscationReflectionHelper.findField(Slot.class, "field_75221_f");
	@SuppressWarnings("java:S3011") //the use of reflection to bypass field invisiblity is intentional and necessary here
	private static void setSlotYPos(Slot slot, int yPos) {
		try {
			SLOT_Y_POS.set(slot, yPos);
		}
		catch (IllegalAccessException e) {
			LogHelper.error("Error setting yPos of Slot: ", e);
		}
	}
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
}
