package xreliquary.items.alkahestry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.lib.Names;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

public class AlkahestryCraftingRecipe implements IRecipe {

    public static Item returnedItem;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack tome = null;
        ItemStack itemStack = null;
        int valid = 0;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(returnedItem))) {
                    tome = stack.copy();
                } else if (!ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(returnedItem))) {
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
            return recipe != null && (NBTHelper.getInteger("redstone", tome) - recipe.cost >= 0);
        } else {
            return false;
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        AlkahestRecipe returned = null;
        ItemStack dictStack = null;
        ItemStack tome = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (stack.getItem() instanceof ItemAlkahestryTome) {
                    tome = stack;
                }
                if (!(ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(returnedItem)))) {
                    if (Alkahestry.getDictionaryKey(stack) == null)
                        returned = Alkahestry.getRegistry().get(ContentHelper.getIdent(stack.getItem()));
                    else {
                        returned = Alkahestry.getDictionaryKey(stack);
                        dictStack = stack;
                    }
                }
            }
        }

        if (dictStack == null) {
            return new ItemStack(returned.item.getItem(), returned.yield + 1, returned.item.getItemDamage());
        } else {
            return new ItemStack(dictStack.getItem(), returned.yield + 1, dictStack.getItemDamage());
        }
    }

    public int getCraftingResultCost(IInventory inv) {
        AlkahestRecipe returned = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (!(ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(returnedItem)))) {
                    if (Alkahestry.getDictionaryKey(stack) == null)
                        returned = Alkahestry.getRegistry().get(ContentHelper.getIdent(stack.getItem()));
                    else {
                        returned = Alkahestry.getDictionaryKey(stack);
                    }
                }
            }
        }
        if (returned == null)
            return 0;
        return returned.cost;
    }


    @SubscribeEvent
    public void onItemCraftedEvent(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting == null)
            return;
        if (event.crafting.getItem() == Items.redstone)
            return;
        for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
            ItemStack stack = event.craftMatrix.getStackInSlot(i);
            if (stack == null)
                continue;
            if (stack.getItem() instanceof ItemAlkahestryTome) {
                NBTHelper.setInteger("redstone", event.craftMatrix.getStackInSlot(i), NBTHelper.getInteger("redstone", event.craftMatrix.getStackInSlot(i)) - getCraftingResultCost(event.craftMatrix));
            }
        }

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
