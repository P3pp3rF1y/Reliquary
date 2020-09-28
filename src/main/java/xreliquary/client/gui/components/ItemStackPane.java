package xreliquary.client.gui.components;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackPane extends Component {
	private ItemStack itemStack;
	private final boolean renderEffect;
	private final boolean renderOverlay;

	public ItemStackPane(Item item) {
		this(new ItemStack(item));
	}
	public ItemStackPane(ItemStack itemStack) {
		this(itemStack, false, false);
	}

	public ItemStackPane(ItemStack itemStack, boolean renderEffect, boolean renderOverlay) {
		this.itemStack = itemStack;
		this.renderEffect = renderEffect;
		this.renderOverlay = renderOverlay;
	}

	public void setItem(Item item) {
		itemStack = new ItemStack(item);
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public int getHeightInternal() {
		return 16;
	}

	@Override
	public int getWidthInternal() {
		return 16;
	}

	@Override
	public void renderInternal(MatrixStack matrixStack, int x, int y) {
		if (itemStack.isEmpty()) {
			return;
		}

		RenderHelper.enableStandardItemLighting();
		Minecraft mc = Minecraft.getInstance();
		if (renderEffect) {
			mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, x, y);
		} else {
			mc.getItemRenderer().renderItemIntoGUI(itemStack, x, y);
		}
		if (renderOverlay) {
			mc.getItemRenderer().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
		}
		RenderHelper.disableStandardItemLighting();
	}
}
