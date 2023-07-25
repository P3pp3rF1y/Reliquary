package reliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import reliquary.reference.Colors;

public class TextPane extends Component {
	private String text;
	private int width;
	private final int textColor;

	public TextPane(String text) {
		this(text, Colors.get(Colors.PURE));
	}
	public TextPane(String text, int textColor) {
		this.text = text;
		width = text.length() * 6;
		this.textColor = textColor;
	}

	public void setText(String text) {
		this.text = text;
		width = Minecraft.getInstance().font.width(text);
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
	public void renderInternal(GuiGraphics guiGraphics, int x, int y) {
		guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, textColor);
	}
}
