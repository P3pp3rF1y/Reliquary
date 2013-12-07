package xreliquary.items.alkahestry;

import xreliquary.Config;
import xreliquary.items.XRItems;
import xreliquary.util.AlkahestRecipe;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AlkahestryRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack tomb = null;
		int itemID = 0;
		boolean isCharging = false;
		int valid = 0;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(stack.itemID == XRItems.alkahestryTome.itemID) {
					tomb = stack.copy();
				} else if (stack.itemID != XRItems.alkahestryTome.itemID && stack.itemID != Block.blockRedstone.blockID) {
					if(valid == 0) {
						valid = 1;
						itemID = stack.itemID;
					} else {
						valid = 2;
					}
				} else {
					isCharging = true;
				}
			}
		}
		if(tomb != null && valid == 1 && !isCharging && AlkahestryRegistry.getRegistry().containsKey(itemID)) {
			AlkahestRecipe recipe = AlkahestryRegistry.getRegistry().get(itemID);
			if(tomb.getItemDamage() + recipe.cost <= Config.tombRedstoneLimit) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		AlkahestRecipe returned = null;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(stack.itemID != XRItems.alkahestryTome.itemID && AlkahestryRegistry.getRegistry().containsKey(stack.itemID)) {
					returned = AlkahestryRegistry.getRegistry().get(stack.itemID);
				}
			}
		}
		return new ItemStack(returned.item.getItem(), returned.yield + 1, returned.item.getItemDamage());
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(XRItems.alkahestryTome, 1);
	}

}
