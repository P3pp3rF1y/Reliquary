package reliquary.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IPedestal {
	BlockPos getBlockPosition();

	int addToConnectedInventory(Level level, ItemStack stack);

	int fillConnectedTank(FluidStack fluidStack, IFluidHandler.FluidAction action);

	int fillConnectedTank(FluidStack fluidStack);

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
