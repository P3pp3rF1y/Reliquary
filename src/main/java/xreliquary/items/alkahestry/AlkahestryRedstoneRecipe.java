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
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;


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
        return tome != null && valid == 1 && NBTHelper.getInteger("redstone", tome) + amount <= Settings.AlkahestryTome.redstoneLimit;
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
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Blocks.glowstone))) {
                    amount += 4;
                } else if (ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Items.redstone)) || ContentHelper.getIdent(stack.getItem()).equals(ContentHelper.getIdent(Items.glowstone_dust))) {
                    amount++;
                }
            }
        }

        NBTHelper.setInteger("redstone", tome, NBTHelper.getInteger("redstone", tome) + amount);
        tome.setItemDamage(tome.getMaxDamage() - NBTHelper.getInteger("redstone", tome));
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
