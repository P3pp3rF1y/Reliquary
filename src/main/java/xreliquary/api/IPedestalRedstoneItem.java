package xreliquary.api;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IPedestalRedstoneItem {
	void updateRedstone(@Nonnull ItemStack stack, IPedestal pedestal);

	void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal);
}
