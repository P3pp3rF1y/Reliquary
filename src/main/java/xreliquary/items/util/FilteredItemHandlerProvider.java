package xreliquary.items.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class FilteredItemHandlerProvider implements ICapabilitySerializable<CompoundNBT> {
	private FilteredItemStackHandler itemHandler;

	public FilteredItemHandlerProvider(List<FilteredItemStackHandler.RemovableStack> filteredBigItemStacks) {
		itemHandler = new FilteredItemStackHandler(filteredBigItemStacks);
	}

	@Override
	public CompoundNBT serializeNBT() {
		return itemHandler.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT tagCompound) {
		itemHandler.deserializeNBT(tagCompound);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, LazyOptional.of(() -> itemHandler));
	}
}
