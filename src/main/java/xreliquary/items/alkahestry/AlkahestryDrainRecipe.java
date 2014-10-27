package xreliquary.items.alkahestry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.util.alkahestry.AlkahestRecipe;
import xreliquary.util.alkahestry.Alkahestry;

public class AlkahestryDrainRecipe implements IRecipe {

    public static Item returnedItem;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean valid = true;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack == null)
                continue;
            if (!(stack.getItem() instanceof ItemAlkahestryTome)) {
                valid = NBTHelper.getInteger("redstone", stack) > 0;
            }
        }
        return valid;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack tome = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (stack.getItem() instanceof ItemAlkahestryTome) {
                    tome = stack;
                }
            }
        }

        if (tome != null) {
            int quantity = NBTHelper.getInteger("redstone", tome);
            quantity = Math.min(quantity, new ItemStack(Items.redstone, 1, 0).getMaxStackSize());

            if (quantity == 0)
                return null;
            return new ItemStack(Items.redstone, quantity);
        }
        return null;
    }


    @SubscribeEvent
    public void onItemCraftedEvent(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting == null)
            return;
        if (event.crafting.getItem() == Items.redstone) {
            for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
                ItemStack stack = event.craftMatrix.getStackInSlot(i);
                if (stack == null)
                    continue;
                if (stack.getItem() instanceof ItemAlkahestryTome) {
                    NBTHelper.setInteger("redstone", event.craftMatrix.getStackInSlot(i), NBTHelper.getInteger("redstone", event.craftMatrix.getStackInSlot(i)) - event.crafting.stackSize);
                }
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
