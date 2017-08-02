package xreliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemStackPane extends Component {
	private ItemStack itemStack;
	private boolean renderEffect;

	public ItemStackPane(ItemStack itemStack) {
		this(itemStack, false);
	}

	public ItemStackPane(ItemStack itemStack, boolean renderEffect) {
		this.itemStack = itemStack;
		this.renderEffect = renderEffect;
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
		RenderHelper.enableGUIStandardItemLighting();
		if (renderEffect) {
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
		} else {
			Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemStack, x, y);
		}
	}
}
