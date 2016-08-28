package xreliquary.items.util.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import xreliquary.init.ModItems;

import javax.annotation.Nullable;

public class FluidHandlerEmperorChalice implements IFluidHandler, ICapabilityProvider {

	private ItemStack chalice;

	public FluidHandlerEmperorChalice(ItemStack chalice) {
		this.chalice = chalice;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[] {new FluidTankProperties(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), Fluid.BUCKET_VOLUME)};
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(!ModItems.emperorChalice.isEnabled(chalice) || resource == null || resource.amount <= 0 || resource.getFluid() != FluidRegistry.WATER)
			return 0;

		return resource.amount;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(ModItems.emperorChalice.isEnabled(chalice) || resource == null || resource.amount <= 0 || resource.getFluid() != FluidRegistry.WATER)
			return new FluidStack(FluidRegistry.WATER, 0);

		return new FluidStack(FluidRegistry.WATER, Math.min(resource.amount, Fluid.BUCKET_VOLUME));
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(ModItems.emperorChalice.isEnabled(chalice))
			return new FluidStack(FluidRegistry.WATER, 0);

		return new FluidStack(FluidRegistry.WATER, Math.min(maxDrain, Fluid.BUCKET_VOLUME));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		//noinspection unchecked
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
	}
}
