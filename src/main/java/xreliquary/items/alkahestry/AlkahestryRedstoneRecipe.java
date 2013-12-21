package xreliquary.items.alkahestry;

import xreliquary.items.XRItems;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AlkahestryRedstoneRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack tomb = null;
		boolean isRedstoneBlock = false;
		int isCharging = 0;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (stack.itemID == XRItems.alkahestryTome.itemID) {
					tomb = stack.copy();
				} else if (stack.itemID == Item.redstone.itemID) {
					if (isCharging == 0) {
						isCharging = 1;
					} else {
						isCharging = 2;
					}
				} else if (stack.itemID == Block.blockRedstone.blockID) {
					if (isCharging == 0) {
						isCharging = 1;
						isRedstoneBlock = true;
					} else {
						isCharging = 2;
					}
				}
			}
		}
		if (tomb != null && isCharging == 1 && tomb.getItemDamage() != 0) {
			if (!isRedstoneBlock) {
				return true;
			} else if (isRedstoneBlock && tomb.getItemDamage() >= 8) {
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
		ItemStack tomb = null;
		boolean isRedstoneBlock = false;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (stack.itemID == XRItems.alkahestryTome.itemID) {
					tomb = stack.copy();
				} else if (stack.itemID == Block.blockRedstone.blockID) {
					isRedstoneBlock = true;
				}
			}
		}

		if (isRedstoneBlock)
			tomb.setItemDamage(tomb.getItemDamage() - 9);
		else
			tomb.setItemDamage(tomb.getItemDamage() - 1);
		return tomb;
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
