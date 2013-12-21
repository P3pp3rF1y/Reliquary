package xreliquary.client.gui;

import xreliquary.lib.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiAlkahestTomb extends GuiContainer {
	
	private static final ResourceLocation BOOK_TEX = new ResourceLocation(Reference.MOD_ID, "/textures/gui/book.png");

	public GuiAlkahestTomb(Container container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.mc.renderEngine.bindTexture(this.BOOK_TEX);
		this.drawTexturedModalRect((this.width - 146) / 2, (this.height - 179) / 2, 0, 0, 146, 179);
	}

}
