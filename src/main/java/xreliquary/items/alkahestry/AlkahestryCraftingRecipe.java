package xreliquary.items.alkahestry;

import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.util.ContentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

public class AlkahestryCraftingRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack tome = null;
        ItemStack itemStack = null;
        int valid = 0;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(ContentHandler.getItem(Names.alkahestry_tome)))) {
                    tome = stack.copy();
                } else if (!ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(ContentHandler.getItem(Names.alkahestry_tome)))) {
                    if (valid == 0) {
                        valid = 1;
                        itemStack = stack;
                    } else {
                        valid = 2;
                    }
                }
            }
        }
        if (tome != null && valid == 1 && itemStack != null) {
            AlkahestRecipe recipe = null;
            if (Alkahestry.getDictionaryKey(itemStack) == null)
                recipe = Alkahestry.getRegistry().get(ContentHelper.getIdent(itemStack.getItem()));
            else
                recipe = Alkahestry.getDictionaryKey(itemStack);
            return recipe != null && (tome.getItemDamage() + recipe.cost <= Reliquary.CONFIG.getInt(Names.alkahestry_tome, "redstoneLimit"));
        } else {
            return false;
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        AlkahestRecipe returned = null;
        ItemStack dictStack = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (!(ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(ContentHandler.getItem(Names.alkahestry_tome))))) {
                    if (Alkahestry.getDictionaryKey(stack) == null)
                        returned = Alkahestry.getRegistry().get(ContentHelper.getIdent(stack.getItem()));
                    else {
                        returned = Alkahestry.getDictionaryKey(stack);
                        dictStack = stack;
                    }
                }
            }
        }

        if (dictStack == null)
            return new ItemStack(returned.item.getItem(), returned.yield + 1, returned.item.getItemDamage());
        else
            return new ItemStack(dictStack.getItem(), returned.yield + 1, dictStack.getItemDamage());
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ContentHandler.getItem(Names.alkahestry_tome), 1);
    }

}
