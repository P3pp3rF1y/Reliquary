package xreliquary.api;

import net.minecraft.item.ItemStack;

public interface IPedestalActionItem {
	void update( ItemStack stack, IPedestal pedestal);
	void onRemoved( ItemStack stack, IPedestal pedestal);
	void stop( ItemStack stack, IPedestal pedestal);
}
