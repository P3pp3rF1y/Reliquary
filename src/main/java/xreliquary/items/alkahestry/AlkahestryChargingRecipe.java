package xreliquary.items.alkahestry;

import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Settings;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;

import java.util.Map;


public class AlkahestryChargingRecipe implements IRecipe {

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
                } else {
                    boolean isChargingItem = false;
                    for (Map.Entry<String, AlkahestChargeRecipe> entry : Settings.AlkahestryTome.chargingRecipes.entrySet()) {
                        AlkahestChargeRecipe recipe = entry.getValue();
                        if (stack.getItem() == recipe.item.getItem() && stack.getMetadata() == recipe.item.getMetadata()) {
                            if (valid == 0)
                                valid = 1;
                            amount += recipe.charge;
                            isChargingItem = true;
                            break;
                        }
                    }

                    if (!isChargingItem) {
                        valid = 2;
                    }
                }
            }
        }
        return tome != null && valid == 1 && NBTHelper.getInteger("charge", tome) + amount <= Settings.AlkahestryTome.chargeLimit;
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
                }
                else {
                    for (Map.Entry<String, AlkahestChargeRecipe> entry : Settings.AlkahestryTome.chargingRecipes.entrySet()) {
                        AlkahestChargeRecipe recipe = entry.getValue();
                        if (stack.getItem() == recipe.item.getItem() && stack.getMetadata() == recipe.item.getMetadata()) {
                            amount += recipe.charge;
                            break;
                        }
                    }
                }
            }
        }

        NBTHelper.setInteger("charge", tome, NBTHelper.getInteger("charge", tome) + amount);
        tome.setItemDamage(tome.getMaxDamage() - NBTHelper.getInteger("charge", tome));
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

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            ItemStack remainingStack = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);

            if (remainingStack != null && remainingStack.getItem() instanceof ItemAlkahestryTome) {
                remainingStack = null;
            }

            aitemstack[i] = remainingStack;
        }

        return aitemstack;
    }
}
