package xreliquary.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public interface IPedestal {
	World getTheWorld();

	BlockPos getBlockPos();

	int addToConnectedInventory(@Nonnull ItemStack stack);

	int fillConnectedTank(FluidStack fluidStack, boolean doFill);

	int fillConnectedTank(FluidStack fluidStack);

	void setActionCoolDown(int coolDownTicks);

	FakePlayer getFakePlayer();

	void destroyCurrentItem();

	void replaceCurrentItem(@Nonnull ItemStack stack);
	List<BlockPos> getPedestalsInRange(int range);
	void switchOn(BlockPos switchedOnFrom);
	void switchOff(BlockPos switchedOffFrom);
	int getCurrentItemIndex();
	Object getItemData(int index);
	void setItemData(int index, Object data);
	boolean switchedOn();
}
