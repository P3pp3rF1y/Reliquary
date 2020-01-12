package xreliquary.api;

import net.minecraft.item.ItemStack;

public interface IPedestalRedstoneItem {
	void updateRedstone( ItemStack stack, IPedestal pedestal);

	void onRemoved( ItemStack stack, IPedestal pedestal);
}
