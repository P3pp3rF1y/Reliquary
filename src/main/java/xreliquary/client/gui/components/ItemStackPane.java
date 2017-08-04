package xreliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackPane extends Component {
	private ItemStack itemStack;
	private boolean renderEffect;
	private boolean renderOverlay;

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
		this.itemStack = new ItemStack(item);
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
	public void renderInternal(int x, int y) {
		if (itemStack.isEmpty())
			return;

		RenderHelper.enableGUIStandardItemLighting();
		Minecraft mc = Minecraft.getMinecraft();
		if (renderEffect) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
		} else {
			mc.getRenderItem().renderItemIntoGUI(itemStack, x, y);
		}
		if (renderOverlay) {
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
		}
	}
}
