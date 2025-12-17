package reliquary.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface IPedestal {
	BlockPos getBlockPosition();

	int addToConnectedInventory(Level level, ItemStack stack);

	default int fillConnectedTank(FluidResource fluidResource, int amount) {
		try (Transaction tx = Transaction.openRoot()) {
			int fluidFilled = fillConnectedTank(fluidResource, amount, tx);
			if (fluidFilled > 0) {
				tx.commit();
			}
			return fluidFilled;
		}
	}

	int fillConnectedTank(FluidResource fluidResource, int amount, Transaction tx);

	void setActionCoolDown(int coolDownTicks);

	Optional<FakePlayer> getFakePlayer();

	void destroyItem();

	void setItem(ItemStack stack);

	ItemStack getItem();

	List<BlockPos> getPedestalsInRange(Level level, int range);

	void switchOn(Level level, BlockPos switchedOnFrom);

	void switchOff(Level level, BlockPos switchedOffFrom);

	Object getItemData();

	void setItemData(@Nullable Object data);

	boolean switchedOn();
}
