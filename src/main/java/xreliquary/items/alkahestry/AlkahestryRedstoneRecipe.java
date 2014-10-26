package xreliquary.items.alkahestry;

import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;

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
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Items.glowstone_dust))) {
                    if (valid == 0)
                        valid = 1;
                    amount++;
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Blocks.glowstone))) {
                    if (valid == 0)
                        valid = 1;
                    amount += 4;
                } else {
                    valid = 2;
                }
            }
        }
        return tome != null && valid == 1 && NBTHelper.getInteger("redstone", tome) + amount <= Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstone_limit");
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

        NBTHelper.setInteger("redstone", tome, NBTHelper.getInteger("redstone", tome) + amount);
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
