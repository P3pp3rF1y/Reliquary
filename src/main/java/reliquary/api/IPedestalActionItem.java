package reliquary.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface IPedestalActionItem {
	void update(ItemStack stack, Level level, IPedestal pedestal);

	void onRemoved(ItemStack stack, Level level, IPedestal pedestal);

	void stop(ItemStack stack, Level level, IPedestal pedestal);

	default Optional<Vec3> getRenderBoundingBoxOuterPosition() {
		return Optional.empty();
	}
}
