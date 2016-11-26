package xreliquary.items.util;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class FilteredItemHandlerProvider implements ICapabilitySerializable<NBTTagCompound> {
	private FilteredItemStackHandler itemHandler;

	public FilteredItemHandlerProvider(int[] limits, Item[] items, int[] unitWorth) {
		itemHandler = new FilteredItemStackHandler(limits, items, unitWorth);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return itemHandler.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound tagCompound) {
		itemHandler.deserializeNBT(tagCompound);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			//noinspection unchecked
			return (T) itemHandler;

		return null;
	}
}
