package xreliquary.items.alkahestry;

import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AlkahestryRedstoneRecipe implements IRecipe {

    public static Item returnedItem;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack tome = null;
        int amount = 0;
        int valid = 0;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(returnedItem))) {
                    tome = stack.copy();
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Items.redstone))) {
                    if (valid == 0)
                        valid = 1;
                    amount++;
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Blocks.redstone_block))) {
                    if (valid == 0)
                        valid = 1;
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
                if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(returnedItem))) {
                    tome = stack.copy();
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Blocks.redstone_block))) {
                    amount += 9;
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Items.redstone))) {
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
        return new ItemStack(returnedItem, 1);
    }

}
