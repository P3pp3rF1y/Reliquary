package xreliquary.items.alkahestry;


import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.alkahestry.Alkahestry;

public class AlkahestryCraftingRecipe implements IRecipe {

    //TODO figure out if I can get rid of this
    public static Item returnedItem = ModItems.alkahestryTome;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        ItemStack tome = null;
        ItemStack itemStack = null;
        int valid = 0;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if ( RegistryHelper.getItemRegistryName(stack.getItem()).equals( RegistryHelper.getItemRegistryName(returnedItem))) {
                    tome = stack.copy();
                } else if (! RegistryHelper.getItemRegistryName(stack.getItem()).equals( RegistryHelper.getItemRegistryName(returnedItem))) {
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
            AlkahestCraftRecipe recipe = null;
            if (Alkahestry.getDictionaryKey(itemStack) == null)
                recipe = Settings.AlkahestryTome.craftingRecipes.get( RegistryHelper.getItemRegistryName(itemStack.getItem()));
            else
                recipe = Alkahestry.getDictionaryKey(itemStack);
            return recipe != null && (NBTHelper.getInteger("charge", tome) - recipe.cost >= 0);
        } else {
            return false;
        }
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        AlkahestCraftRecipe returned = null;
        ItemStack dictStack = null;
        ItemStack tome = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (stack.getItem() instanceof ItemAlkahestryTome) {
                    tome = stack;
                }
                if (!( RegistryHelper.getItemRegistryName(stack.getItem()).equals( RegistryHelper.getItemRegistryName(returnedItem)))) {
                    if (Alkahestry.getDictionaryKey(stack) == null)
                        returned = Settings.AlkahestryTome.craftingRecipes.get( RegistryHelper.getItemRegistryName(stack.getItem()));
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
        AlkahestCraftRecipe returned = null;
        for (int count = 0; count < inv.getSizeInventory(); count++) {
            ItemStack stack = inv.getStackInSlot(count);
            if (stack != null) {
                if (!( RegistryHelper.getItemRegistryName(stack.getItem()).equals( RegistryHelper.getItemRegistryName(returnedItem)))) {
                    if (Alkahestry.getDictionaryKey(stack) == null)
                        returned = Settings.AlkahestryTome.craftingRecipes.get( RegistryHelper.getItemRegistryName(stack.getItem()));
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
                NBTHelper.setInteger("charge", remainingStack, NBTHelper.getInteger("charge", remainingStack) - getCraftingResultCost(inv));
                remainingStack.setItemDamage(remainingStack.getMaxDamage() - NBTHelper.getInteger("charge", remainingStack));
            }
            aitemstack[i] = remainingStack;
        }

        return aitemstack;
    }
}
