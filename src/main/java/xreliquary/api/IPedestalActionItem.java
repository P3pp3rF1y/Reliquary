package xreliquary.api;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IPedestalActionItem {
	void update(@Nonnull ItemStack stack, IPedestal pedestal);
	void onRemoved(@Nonnull ItemStack stack, IPedestal pedestal);
	void stop(@Nonnull ItemStack stack, IPedestal pedestal);
}
