package xreliquary.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IPedestal {
	World getWorld();
	BlockPos getPos();
	int addToConnectedInventory(ItemStack stack);
}
