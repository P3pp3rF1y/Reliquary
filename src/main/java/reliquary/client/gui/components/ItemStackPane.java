package reliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemStackPane extends Component {
	private ItemStack itemStack;
	private final boolean renderOverlay;

	public ItemStackPane(Item item) {
		this(new ItemStack(item));
	}

	public ItemStackPane(ItemStack itemStack) {
		this(itemStack, false);
	}

	public ItemStackPane(ItemStack itemStack, boolean renderOverlay) {
		this.itemStack = itemStack;
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
	public void renderInternal(GuiGraphics guiGraphics, int x, int y) {
		if (itemStack.isEmpty()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		guiGraphics.renderItem(itemStack, x, y);
		if (renderOverlay) {
			guiGraphics.renderItemDecorations(mc.font, itemStack, x, y, null);
		}
	}
}
