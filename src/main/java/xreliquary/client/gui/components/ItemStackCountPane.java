package xreliquary.client.gui.components;

import net.minecraft.item.ItemStack;
import xreliquary.reference.Colors;

public class ItemStackCountPane extends Component {
	private TextPane countPane;
	private Box box;

	public ItemStackCountPane(ItemStack itemStack, int count) {
		this(itemStack, count, Colors.get(Colors.PURE));
	}

	public ItemStackCountPane(ItemStack itemStack, int count, int textColor) {
		this(Box.Layout.HORIZONTAL, itemStack, count, textColor);
	}
	public ItemStackCountPane(Box.Layout layout, ItemStack itemStack, int count, int textColor) {
		countPane = new TextPane(String.valueOf(count), textColor);
		box = new Box(layout, layout == Box.Layout.VERTICAL ? Box.Alignment.MIDDLE : Box.Alignment.MIDDLE, new ItemStackPane(itemStack), countPane);
	}

	public void setCount(int count) {
		countPane.setText(String.valueOf(count));
	}

	@Override
	public boolean hasChanged() {
		return box.hasChanged();
	}

	@Override
	public void setChanged(boolean changed) {
		box.setChanged(changed);
	}

	@Override
	protected void refresh() {
		box.refresh();
	}

	@Override
	public int getPadding() {
		return box.getPadding();
	}

	@Override
	public int getHeightInternal() {
		return box.getHeightInternal();
	}

	@Override
	public int getWidthInternal() {
		return box.getWidthInternal();
	}

	@Override
	public void renderInternal(int x, int y) {
		box.renderInternal(x, y);
	}
}
