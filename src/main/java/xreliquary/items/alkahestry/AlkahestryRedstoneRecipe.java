package xreliquary.items.alkahestry;

import xreliquary.init.AbstractionHandler;
import xreliquary.lib.Names;
import xreliquary.util.ObjectUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AlkahestryRedstoneRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
        ItemStack tomb = null;
		boolean isRedstoneBlock = false;
		int valid = 0;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(AbstractionHandler.getItem(Names.TOME_NAME)))) {
					tomb = stack.copy();
				} else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(Items.redstone))) {
					if (valid == 0) {
						valid = 1;
					} else {
                        valid = 2;
					}
				} else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
					if (valid == 0) {
                        valid = 1;
						isRedstoneBlock = true;
					} else {
                        valid = 2;
					}
				}
			}
		}
        if (tomb != null && valid == 1 && tomb.getItemDamage() != 0) {
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
				if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(AbstractionHandler.getItem(Names.TOME_NAME)))) {
					tomb = stack.copy();
				} else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
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
		return new ItemStack(AbstractionHandler.getItem(Names.TOME_NAME), 1);
	}

}
