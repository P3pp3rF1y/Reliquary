package reliquary.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IPedestalActionItem {
	void update(ItemStack stack, Level level, IPedestal pedestal);

	void onRemoved(ItemStack stack, Level level, IPedestal pedestal);

	void stop(ItemStack stack, Level level, IPedestal pedestal);
}
