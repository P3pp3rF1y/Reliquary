package xreliquary.blocks.tile;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

public class PedestalFluidHandler implements IFluidHandler {
	private final PedestalBlockEntity pedestal;

	PedestalFluidHandler(PedestalBlockEntity pedestal) {
		this.pedestal = pedestal;
	}

	@Override
	public int getTanks() {
		return getFluidHandlerValue(IFluidHandler::getTanks).orElse(0);
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return getFluidHandlerValue(fh -> fh.getFluidInTank(tank)).orElse(FluidStack.EMPTY);
	}

	@Override
	public int getTankCapacity(int tank) {
		return getFluidHandlerValue(fh -> fh.getTankCapacity(tank)).orElse(0);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return getFluidHandlerValue(fh -> fh.isFluidValid(tank, stack)).orElse(false);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return getFluidHandlerValue(fh -> executeAndUpdateItem(fh, f -> f.fill(resource, action))).orElse(0);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return getFluidHandlerValue(fh -> executeAndUpdateItem(fh, f -> f.drain(resource, action))).orElse(FluidStack.EMPTY);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return getFluidHandlerValue(fh -> executeAndUpdateItem(fh, f -> f.drain(maxDrain, action))).orElse(FluidStack.EMPTY);
	}

	private <T> T executeAndUpdateItem(IFluidHandler fh, Function<IFluidHandler, T> execute) {
		T ret = execute.apply(fh);
		if (fh instanceof IFluidHandlerItem fhi && fhi.getContainer() != pedestal.getItem()) {
			pedestal.setItem(fhi.getContainer());
		}
		return ret;
	}

	private <T> Optional<T> getFluidHandlerValue(Function<IFluidHandler, T> mapValue) {
		ItemStack fluidContainer = pedestal.getFluidContainer();
		if (fluidContainer.isEmpty()) {
			return Optional.empty();
		}
		LazyOptional<IFluidHandler> cap = fluidContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
		if (cap.isPresent()) {
			return cap.map(fh -> Optional.of(mapValue.apply(fh))).orElse(Optional.empty());
		}
		return fluidContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(fh -> Optional.of(mapValue.apply(fh))).orElse(Optional.empty());

	}
}
