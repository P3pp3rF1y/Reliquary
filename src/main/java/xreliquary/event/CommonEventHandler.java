package xreliquary.event;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import mods.themike.core.util.ObjectUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import xreliquary.items.XRItems;
import xreliquary.items.alkahestry.Alkahestry;
import xreliquary.lib.Reference;
import xreliquary.util.AlkahestRecipe;

public class CommonEventHandler {

	@SubscribeEvent
	public void onCraftingPotion(PlayerEvent.ItemCraftedEvent event) {
		for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
			if (event.craftMatrix.getStackInSlot(slot) == null) {
				continue;
			}
			if (event.craftMatrix.getStackInSlot(slot).getItem() == XRItems.glowingWater)
				if (!event.player.inventory.addItemStackToInventory(new ItemStack(XRItems.condensedPotion, 1, Reference.EMPTY_VIAL_META))) {
					event.player.entityDropItem(new ItemStack(XRItems.condensedPotion, 1, Reference.EMPTY_VIAL_META), 0.1F);
				}
		}
	}

    @SubscribeEvent
    public void onCraftingAlkahest(PlayerEvent.ItemCraftedEvent event) {
        boolean isCharging = false;
        int tomb = 9;
        AlkahestRecipe recipe = null;
        for (int count = 0; count < event.craftMatrix.getSizeInventory(); count++) {
            ItemStack stack = event.craftMatrix.getStackInSlot(count);
            if (stack != null) {
                if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(XRItems.alkahestryTome))) {
                    tomb = count;
                } else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(Items.redstone)) || ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
                    isCharging = true;
                } else {
                    if (Alkahestry.getDictionaryKey(stack) == null) {
                        recipe = Alkahestry.getRegistry().get(ObjectUtils.getItemIdentifier(stack.getItem()));
                    } else {
                        recipe = Alkahestry.getDictionaryKey(stack);
                    }
                }
            }
        }
        if (tomb != 9 && isCharging) {
            event.craftMatrix.setInventorySlotContents(tomb, null);
        } else if (tomb != 9 && !isCharging && recipe != null) {
            ItemStack temp = event.craftMatrix.getStackInSlot(tomb);
            temp.setItemDamage(temp.getItemDamage() + recipe.cost);
            event.craftMatrix.setInventorySlotContents(tomb, temp);
        }
    }

}
