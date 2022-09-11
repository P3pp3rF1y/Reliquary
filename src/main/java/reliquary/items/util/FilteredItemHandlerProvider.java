package reliquary.items.util;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class FilteredItemHandlerProvider implements ICapabilitySerializable<CompoundTag> {
	private final FilteredItemStackHandler itemHandler;

	public FilteredItemHandlerProvider(List<FilteredItemStack> filteredItemStacks) {
		itemHandler = new FilteredItemStackHandler(filteredItemStacks);
	}

	@Override
	public CompoundTag serializeNBT() {
		return itemHandler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag tagCompound) {
		itemHandler.deserializeNBT(tagCompound);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> itemHandler));
	}
}
