package xreliquary.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import xreliquary.blocks.tile.TileEntityPedestal;

public interface IPedestalRedstoneItem {
	void updateRedstone(ItemStack stack, IPedestal pedestal);
	void onRemoved(ItemStack stack, IPedestal pedestal);
}
