package xreliquary.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

public interface IPedestal {
	World getTheWorld();

	BlockPos getBlockPos();

	int addToConnectedInventory(ItemStack stack);

	int fillConnectedTank(FluidStack fluidStack, IFluidHandler.FluidAction action);

	int fillConnectedTank(FluidStack fluidStack);

	void setActionCoolDown(int coolDownTicks);

	FakePlayer getFakePlayer();

	void destroyItem();

	void setItem(ItemStack stack);

	ItemStack getItem();

	List<BlockPos> getPedestalsInRange(int range);

	void switchOn(BlockPos switchedOnFrom);

	void switchOff(BlockPos switchedOffFrom);

	Object getItemData();

	void setItemData(@Nullable Object data);

	boolean switchedOn();
}
