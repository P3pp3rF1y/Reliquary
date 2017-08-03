package xreliquary.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import xreliquary.reference.Reference;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class XPBarPane extends Component {
	private static final ResourceLocation XP_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/xp_bar.png");
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
	public void renderInternal(int x, int y) {
		Minecraft.getMinecraft().renderEngine.bindTexture(XP_BAR);
		drawTexturedModalRect(x, y, 0, 0, 11, 74, 22, 74);

		if(xpRatio > 0) {
			int filledHeight = (int) (xpRatio * 74);
			drawTexturedModalRect(x, y + (74 - filledHeight), 11, 74 - filledHeight, 11, filledHeight, 22, 74);
		}
	}
}
