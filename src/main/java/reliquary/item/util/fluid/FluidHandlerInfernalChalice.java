package reliquary.item.util.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import reliquary.init.ModDataComponents;
import reliquary.init.ModItems;
import reliquary.reference.Config;

public class FluidHandlerInfernalChalice implements ResourceHandler<FluidResource> {
	private final ItemStack chalice;
	private final ItemAccess itemAccess;
	private final Journal journal = new Journal();

	public FluidHandlerInfernalChalice(ItemStack chalice, ItemAccess itemAccess) {
		this.chalice = chalice;
		this.itemAccess = itemAccess;
	}

	@Override
	public boolean isValid(int index, FluidResource fluidResource) {
		return fluidResource.is(Fluids.LAVA);
	}

	@Override
	public long getCapacityAsLong(int i, FluidResource fluidResource) {
		return Config.COMMON.items.infernalChalice.fluidLimit.get();
	}

	@Override
	public FluidResource getResource(int index) {
		return FluidResource.of(Fluids.LAVA);
	}

	@Override
	public long getAmountAsLong(int i) {
		return getAmount();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public int insert(int index, FluidResource fluidResource, int amount, TransactionContext tx) {
		if (!ModItems.INFERNAL_CHALICE.get().isEnabled(chalice) || fluidResource.getFluid() != Fluids.LAVA || index != 0) {
			return 0;
		}

		int filled = Math.min(amount, (int) (getCapacityAsLong(index, fluidResource) - getAmount()));
		setAmount(getAmount() + filled, tx);
		return filled;
	}

	@Override
	public int extract(int index, FluidResource fluidResource, int amount, TransactionContext tx) {
		if (ModItems.INFERNAL_CHALICE.get().isEnabled(chalice) || fluidResource.getFluid() != Fluids.LAVA || index != 0) {
			return 0;
		}

		int extracted = Math.min(amount, getAmount());
		setAmount(getAmount() - extracted, tx);
		return extracted;
	}

	private int getAmount() {
		return chalice.getOrDefault(ModDataComponents.FLUID_CONTENTS, SimpleFluidContent.EMPTY).getAmount();
	}

	private void setAmount(int amount, TransactionContext tx) {
		journal.updateSnapshots(tx);
		chalice.set(ModDataComponents.FLUID_CONTENTS, SimpleFluidContent.copyOf(new FluidStack(Fluids.LAVA, amount)));
		itemAccess.exchange(ItemResource.of(chalice), 1, tx);
	}

	private class Journal extends SnapshotJournal<Integer> {
		@Override
		protected Integer createSnapshot() {
			return getAmount();
		}

		@Override
		protected void revertToSnapshot(Integer value) {
			chalice.set(ModDataComponents.FLUID_CONTENTS, SimpleFluidContent.copyOf(new FluidStack(Fluids.LAVA, value)));
		}
	}
}
