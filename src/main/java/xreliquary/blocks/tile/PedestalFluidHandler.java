package xreliquary.blocks.tile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PedestalFluidHandler implements IFluidHandler {
	TileEntityPedestal pedestal;

	public PedestalFluidHandler(TileEntityPedestal pedestal) {
		this.pedestal = pedestal;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		List<ItemStack> fluidContainers = pedestal.getFluidContainers();
		if(fluidContainers.size() == 0)
			return null;

		List<IFluidTankProperties> props = new ArrayList<>();
		for(ItemStack container : fluidContainers) {
			IFluidTankProperties[] containerProps = getContainerTankProperties(container);
			for(int i = 0; i < containerProps.length; i++) {
				props.add(containerProps[i]);
			}
		}

		return props.toArray(new IFluidTankProperties[0]);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		int totalFilled = 0;
		FluidStack resourceCopy = resource.copy();
		for(ItemStack container : pedestal.getFluidContainers()) {
			totalFilled += getFluidCapFromContainer(container).fill(resourceCopy, doFill);
			resourceCopy.amount = resource.amount - totalFilled;

			if(totalFilled >= resource.amount)
				break;
		}

		return totalFilled;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		int totalDrained = 0;
		for(ItemStack container : pedestal.getFluidContainers()) {
			FluidStack drainedStack = getFluidCapFromContainer(container).drain(resource.amount - totalDrained, doDrain);

			if(drainedStack == null)
				continue;

			totalDrained += drainedStack.amount;

			if(totalDrained >= resource.amount)
				break;
		}

		return new FluidStack(resource.getFluid(), totalDrained);
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		List<ItemStack> fluidContainers = pedestal.getFluidContainers();
		if(fluidContainers.size() == 0)
			return null;

		ItemStack container = fluidContainers.get(0);
		Fluid fluid = getFirstContainerFluid(container);

		if(fluid == null)
			return null;

		return drain(new FluidStack(fluid, maxDrain), doDrain);
	}

	private IFluidHandler getFluidCapFromContainer(ItemStack container) {
		if(!container.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
			return null;

		return container.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	}

	private IFluidTankProperties[] getContainerTankProperties(ItemStack container) {
		IFluidHandler handler = getFluidCapFromContainer(container);

		if(handler == null)
			return null;

		return handler.getTankProperties();
	}

	private Fluid getFirstContainerFluid(ItemStack container) {
		IFluidHandler handler = getFluidCapFromContainer(container);

		if(handler == null)
			return null;

		IFluidTankProperties[] tankProperties = handler.getTankProperties();

		if(tankProperties == null || tankProperties.length == 0)
			return null;

		return tankProperties[0].getContents() == null ? null : tankProperties[0].getContents().getFluid();
	}
}
