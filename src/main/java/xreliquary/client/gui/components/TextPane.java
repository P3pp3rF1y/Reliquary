package xreliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import xreliquary.reference.Colors;

public class TextPane extends Component {
	private String text;
	private int width;
	private final int textColor;
	private boolean changed = false;

	public TextPane(String text) {
		this(text, Colors.get(Colors.PURE));
	}
	public TextPane(String text, int textColor) {
		this.text = text;
		this.width = text.length() * 6;
		this.textColor = textColor;
	}

	public void setText(String text) {
		this.text = text;
		changed = true;
	}

	@Override
	protected void refresh() {
		this.width = text.length() * 6;
		changed = false;
	}

	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public int getHeightInternal() {
		return 7;
	}

	@Override
	public int getWidthInternal() {
		return width;
	}

	@Override
	public void renderInternal(int x, int y) {
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, textColor);
	}
}
