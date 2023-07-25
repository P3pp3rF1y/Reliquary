package reliquary.items.util;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

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
		return ForgeCapabilities.ITEM_HANDLER.orEmpty(capability, LazyOptional.of(() -> itemHandler));
	}
}
