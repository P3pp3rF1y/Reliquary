package reliquary.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import reliquary.Reliquary;

public class XPBarPane extends Component {
	private static final ResourceLocation XP_BAR = Reliquary.getRL("textures/gui/xp_bar.png");
	private float xpRatio;

	public void setXpRatio(float xpRatio) {
		this.xpRatio = xpRatio;
	}

	@Override
	public int getHeightInternal() {
		return 74;
	}

	@Override
	public int getWidthInternal() {
		return 11;
	}

	@Override
	public int getPadding() {
		return 2;
	}

	@Override
	public void renderInternal(GuiGraphics guiGraphics, int x, int y) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, XP_BAR, x, y, 0, 0, 11, 74, 22, 74);

		if (xpRatio > 0) {
			int filledHeight = (int) (xpRatio * 74);
			guiGraphics.blit(RenderPipelines.GUI_TEXTURED, XP_BAR, x, y + (74 - filledHeight), 11, 74 - filledHeight, 11, filledHeight, 22, 74);
		}
	}
}
