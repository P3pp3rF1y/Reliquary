package reliquary.item.util.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import reliquary.init.ModItems;

public class FluidHandlerEmperorChalice implements ResourceHandler<FluidResource> {
	private static final FluidResource WATER_RESOURCE = FluidResource.of(Fluids.WATER);
	private final ItemStack chalice;

	public FluidHandlerEmperorChalice(ItemStack chalice) {
		this.chalice = chalice;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public FluidResource getResource(int index) {
		return WATER_RESOURCE;
	}

	@Override
	public long getAmountAsLong(int index) {
		return ModItems.EMPEROR_CHALICE.get().isEnabled(chalice) ? 0 : FluidType.BUCKET_VOLUME;
	}

	@Override
	public long getCapacityAsLong(int index, FluidResource fluidResource) {
		return FluidType.BUCKET_VOLUME;
	}

	@Override
	public boolean isValid(int index, FluidResource fluidResource) {
		return fluidResource.getFluid() == Fluids.WATER;
	}

	@Override
	public int insert(int index, FluidResource fluidResource, int amount, TransactionContext transactionContext) {
		return ModItems.EMPEROR_CHALICE.get().isEnabled(chalice) && fluidResource.getFluid() == Fluids.WATER ? amount : 0;
	}

	@Override
	public int extract(int index, FluidResource fluidResource, int amount, TransactionContext transactionContext) {
		return ModItems.EMPEROR_CHALICE.get().isEnabled(chalice) || fluidResource.getFluid() != Fluids.WATER ? 0 : Math.min(FluidType.BUCKET_VOLUME, amount);
	}
}
