package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import xreliquary.lib.PotionData;
import cpw.mods.fml.common.ICraftingHandler;

public class WaterHandler implements ICraftingHandler {

    @Override
    public void onCrafting(EntityPlayer player, ItemStack item,
            IInventory craftMatrix) {
        for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
            if (craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            if (craftMatrix.getStackInSlot(slot).getItem() == XRItems.glowingWater)
                if (!player.inventory.addItemStackToInventory(new ItemStack(
                        XRItems.condensedPotion, 1, PotionData.EMPTY_VIAL_META))) {
                    player.dropPlayerItem(new ItemStack(
                            XRItems.condensedPotion, 1,
                            PotionData.EMPTY_VIAL_META));
                }
        }
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {
    }

}
