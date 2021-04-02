package xreliquary.items.util.fluid;

import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import xreliquary.init.ModItems;

import javax.annotation.Nullable;

public class FluidHandlerEmperorChalice implements IFluidHandlerItem, ICapabilityProvider {

	public static final FluidStack BUCKET_OF_WATER = new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME);
	private final ItemStack chalice;

	public FluidHandlerEmperorChalice(ItemStack chalice) {
		this.chalice = chalice;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
	}

	@Override
	public ItemStack getContainer() {
		return chalice;
	}

	@Override
	public int getTanks() {
		return 1;
	}


	@Override
	public FluidStack getFluidInTank(int tank) {
		return BUCKET_OF_WATER;
	}

	@Override
	public int getTankCapacity(int tank) {
		return FluidAttributes.BUCKET_VOLUME;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return stack.getFluid() == Fluids.WATER;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (!ModItems.EMPEROR_CHALICE.get().isEnabled(chalice) || resource.isEmpty() || resource.getFluid() != Fluids.WATER) {
			return 0;
		}
		return resource.getAmount();
	}


	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (ModItems.EMPEROR_CHALICE.get().isEnabled(chalice) || resource.isEmpty() || resource.getFluid() != Fluids.WATER) {
			return FluidStack.EMPTY;
		}
		return new FluidStack(Fluids.WATER, Math.min(FluidAttributes.BUCKET_VOLUME, resource.getAmount()));
	}


	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (ModItems.EMPEROR_CHALICE.get().isEnabled(chalice)) {
			return FluidStack.EMPTY;
		}
		return new FluidStack(Fluids.WATER, Math.min(FluidAttributes.BUCKET_VOLUME, maxDrain));
	}
}
