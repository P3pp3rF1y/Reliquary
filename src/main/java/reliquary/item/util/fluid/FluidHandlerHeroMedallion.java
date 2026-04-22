package reliquary.item.util.fluid;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import reliquary.init.ModFluids;
import reliquary.init.ModItems;
import reliquary.util.XpHelper;

public class FluidHandlerHeroMedallion implements ResourceHandler<FluidResource> {
	private static final int MAX_CAPACITY = Integer.MAX_VALUE;
	public static final FluidResource XP_FLUID = FluidResource.of(ModFluids.XP_STILL.get());
	private final ItemStack heroMedallion;
	private final ItemAccess itemAccess;
	private final Journal journal = new Journal();

	public FluidHandlerHeroMedallion(ItemStack heroMedallion, ItemAccess itemAccess) {
		this.heroMedallion = heroMedallion;
		this.itemAccess = itemAccess;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public FluidResource getResource(int i) {
		return XP_FLUID;
	}

	@Override
	public long getAmountAsLong(int index) {
		return XpHelper.experienceToLiquid(getMedallionXp());
	}

	@Override
	public long getCapacityAsLong(int i, FluidResource fluidResource) {
		return MAX_CAPACITY;
	}

	@Override
	public boolean isValid(int i, FluidResource fluidResource) {
		return isXpJuiceFluid(fluidResource);
	}

	private boolean isXpJuiceFluid(FluidResource resource) {
		return resource.is(ModFluids.EXPERIENCE_TAG);
	}

	@Override
	public int extract(int index, FluidResource resource, int amount, TransactionContext tx) {
		if (index != 0 || !isXpJuiceFluid(resource)) {
			return 0;
		}

		int currentXp = getMedallionXp();
		int currentLiquidXp = XpHelper.experienceToLiquid(currentXp);
		int drained = Math.min(currentLiquidXp, amount);

		journal.updateSnapshots(tx);
		ModItems.HERO_MEDALLION.get().setExperience(heroMedallion, currentXp - XpHelper.liquidToExperience(drained));
		itemAccess.exchange(ItemResource.of(heroMedallion), 1, tx);

		return drained;
	}

	@Override
	public int insert(int index, FluidResource resource, int amount, TransactionContext tx) {
		if (index != 0 || !isXpJuiceFluid(resource)) {
			return 0;
		}

		int currentXp = getMedallionXp();
		int toFill = Math.min(MAX_CAPACITY - XpHelper.experienceToLiquid(currentXp), amount);

		journal.updateSnapshots(tx);
		ModItems.HERO_MEDALLION.get().setExperience(heroMedallion, currentXp + XpHelper.liquidToExperience(toFill));
		itemAccess.exchange(ItemResource.of(heroMedallion), 1, tx);
		return toFill;
	}

	private int getMedallionXp() {
		return ModItems.HERO_MEDALLION.get().getExperience(heroMedallion);
	}

	private class Journal extends SnapshotJournal<Integer> {

		@Override
		protected Integer createSnapshot() {
			return getMedallionXp();
		}

		@Override
		protected void revertToSnapshot(Integer value) {
			ModItems.HERO_MEDALLION.get().setExperience(heroMedallion, value);
		}
	}
}
