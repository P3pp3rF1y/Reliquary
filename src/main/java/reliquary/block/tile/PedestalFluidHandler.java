package reliquary.block.tile;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Optional;
import java.util.function.Function;

public class PedestalFluidHandler implements ResourceHandler<FluidResource> {
	private final PedestalBlockEntity pedestal;

	PedestalFluidHandler(PedestalBlockEntity pedestal) {
		this.pedestal = pedestal;
	}

	@Override
	public int size() {
		return getFluidHandlerValue(ResourceHandler::size).orElse(0);
	}

	@Override
	public FluidResource getResource(int index) {
		return getFluidHandlerValue(fh -> fh.getResource(index)).orElse(FluidResource.EMPTY);
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource fluidResource) {
		return getFluidHandlerValue(fh -> fh.getCapacityAsLong(index, fluidResource)).orElse(0L);
	}

	@Override
	public long getAmountAsLong(int i) {
		return getFluidHandlerValue(fh -> fh.getAmountAsLong(i)).orElse(0L);
	}

	@Override
	public boolean isValid(int i, FluidResource fluidResource) {
		return getFluidHandlerValue(fh -> fh.isValid(i, fluidResource)).orElse(false);
	}

	@Override
	public int insert(int index, FluidResource fluidResource, int amount, TransactionContext tx) {
		return getFluidHandlerValue(fh -> fh.insert(index, fluidResource, amount, tx)).orElse(0);
	}

	@Override
	public int extract(int index, FluidResource fluidResource, int amount, TransactionContext tx) {
		return getFluidHandlerValue(fh -> fh.extract(index, fluidResource, amount, tx)).orElse(0);
	}

	private <T> Optional<T> getFluidHandlerValue(Function<ResourceHandler<FluidResource>, T> mapValue) {
		ResourceHandler<FluidResource> fh = ItemAccess.forHandlerIndex(pedestal.getItemHandler(), 0).getCapability(Capabilities.Fluid.ITEM);
		if (fh == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(mapValue.apply(fh));
	}
}
