package xreliquary.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IPedestal {
	World getWorld();
	BlockPos getPos();
	int addToConnectedInventory(ItemStack stack);
	int fillConnectedTank(FluidStack fluidStack, boolean doFill);
	int fillConnectedTank(FluidStack fluidStack);
	void setActionCoolDown(int coolDownTicks);
	FakePlayer getFakePlayer();
	void destroyCurrentItem();
	void replaceCurrentItem(ItemStack stack);
}
