package xreliquary.items.alkahestry;

import xreliquary.init.ContentHandler;
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
        ItemStack tome = null;
        int amount = 0;
		int valid = 0;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome)))) {
					tome = stack.copy();
				} else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(Items.redstone))) {
					if (valid == 0) valid = 1;
                    amount++;
                } else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
                    if (valid == 0) valid = 1;
                    amount += 9;
                } else {
                    valid = 2;
                }
			}
		}
        if (tome != null && valid == 1 && tome.getItemDamage() != 0) {
            if (tome.getItemDamage() >= amount) {
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
		ItemStack tome = null;
        int amount = 0;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome)))) {
					tome = stack.copy();
				} else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
					amount += 9;
                } else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(Items.redstone))) {
                    amount++;
                }
			}
		}

		tome.setItemDamage(tome.getItemDamage() - amount);
		return tome;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ContentHandler.getItem(Names.alkahest_tome), 1);
	}

}
