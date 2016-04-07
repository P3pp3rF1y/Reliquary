package xreliquary.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public interface IPedestal {
	World getTheWorld();
	BlockPos getBlockPos();
	int addToConnectedInventory(ItemStack stack);
	int fillConnectedTank(FluidStack fluidStack, boolean doFill);
	int fillConnectedTank(FluidStack fluidStack);
	void setActionCoolDown(int coolDownTicks);
	FakePlayer getFakePlayer();
	void destroyCurrentItem();
	void replaceCurrentItem(ItemStack stack);
	List<BlockPos> getPedestalsInRange(int range);
	void switchOn(BlockPos switchedOnFrom);
	void switchOff(BlockPos switchedOffFrom);
}
