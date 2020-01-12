package xreliquary.api.client;

import net.minecraft.item.ItemStack;
import xreliquary.blocks.tile.PedestalTileEntity;
import xreliquary.client.render.PedestalRenderer;

public interface IPedestalItemRenderer {
	void doRender(PedestalRenderer tesr, PedestalTileEntity te,  ItemStack stack, double x, double y, double z, float partialTicks, int destroyStage);
}
