package xreliquary.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

public class AlkahestHandler implements ICraftingHandler {
    // if the item is alkahest, this is going to wipe THE ENTIRE crafting
    // matrix, no matter what!
    // it yields reagents only if the player provides enough materials, at 25%
    // of their base value.
    @Override
    public void onCrafting(EntityPlayer player, ItemStack item,
            IInventory craftMatrix) {
        if (item == null)
            return;
        if (item.getItem() != XRItems.alkahest)
            return;
        int totalPoints = 0;
        for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
            if (craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            int id = craftMatrix.getStackInSlot(slot).itemID;
            int value = AlkahestMap.getMappingValue(id);
            System.out.println("Slot " + slot + " contains an item worth "
                    + value);
            totalPoints += value;
            while (craftMatrix.getStackInSlot(slot).stackSize > 1) {
                totalPoints += value;
                craftMatrix.decrStackSize(slot, 1);
            }
        }
        System.out.println("At the time of execution, total value sits at "
                + totalPoints);
        while (totalPoints >= 2304) {
            if (!player.inventory.addItemStackToInventory(new ItemStack(
                    Block.blockLapis, 1))) {
                player.dropPlayerItem(new ItemStack(Block.blockLapis, 1));
            }
            totalPoints -= 2304;
        }
        while (totalPoints >= 256) {
            if (!player.inventory.addItemStackToInventory(new ItemStack(
                    Item.dyePowder, 1, 4))) {
                player.dropPlayerItem(new ItemStack(Item.dyePowder, 1, 4));
            }
            totalPoints -= 256;
        }
        while (totalPoints >= 64) {
            if (!player.inventory.addItemStackToInventory(new ItemStack(
                    Item.redstone, 1))) {
                player.dropPlayerItem(new ItemStack(Item.redstone, 1));
            }
            totalPoints -= 64;
        }
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {
        // n/a
    }

}
