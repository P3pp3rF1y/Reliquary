package xreliquary.items.alkahestry;

import xreliquary.items.XRItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

public class XRAlkahestryCraftingHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory inv) {
		int tomb = 9;
		boolean valid = false;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(stack.itemID == XRItems.alkahestryTome.itemID) {
					tomb = count;
				} else if(stack.itemID == Item.redstone.itemID || stack.itemID == Block.blockRedstone.blockID) {
					valid = true;
				}
			}
		}
		if(tomb != 9 && valid == true) {
			inv.setInventorySlotContents(tomb, null);
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
		
	}

}
