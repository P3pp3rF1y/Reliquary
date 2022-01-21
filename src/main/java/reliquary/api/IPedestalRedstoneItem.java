package reliquary.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IPedestalRedstoneItem {
	void updateRedstone(ItemStack stack, Level level, IPedestal pedestal);

	void onRemoved(ItemStack stack, Level level, IPedestal pedestal);
}
