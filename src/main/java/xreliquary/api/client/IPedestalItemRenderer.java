package xreliquary.api.client;

import net.minecraft.item.ItemStack;
import xreliquary.blocks.tile.TileEntityPedestal;
import xreliquary.client.render.TileEntityPedestalRenderer;

public interface IPedestalItemRenderer {
	void doRender(TileEntityPedestalRenderer tesr, TileEntityPedestal te, ItemStack stack, double x, double y, double z, float partialTicks, int destroyStage);
}
