package xreliquary.items.alkahestry;

import xreliquary.items.XRItems;
import xreliquary.util.AlkahestRecipe;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

public class AlkahestryCraftingHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory inv) {
		boolean isCharging = false;
		int tomb = 9;
		AlkahestRecipe recipe = null;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(stack.itemID == XRItems.alkahestryTome.itemID) {
					tomb = count;
				} else if(stack.itemID == Item.redstone.itemID || stack.itemID == Block.blockRedstone.blockID) {
					isCharging = true;
				} else {
					if(Alkahestry.getDictionaryKey(stack) == null) {
						recipe = Alkahestry.getRegistry().get(stack.itemID);
					} else {
						recipe = Alkahestry.getDictionaryKey(stack);
					}
				}
			}
		}
		if(tomb != 9 && isCharging) {
			inv.setInventorySlotContents(tomb, null);
		} else if(tomb != 9 && !isCharging && recipe != null) {
			ItemStack temp = inv.getStackInSlot(tomb);
			temp.setItemDamage(temp.getItemDamage() + recipe.cost);
			inv.setInventorySlotContents(tomb, temp);
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
		
	}

}
