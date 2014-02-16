package xreliquary.items.alkahestry;

import mods.themike.core.util.ObjectUtils;
import net.minecraft.init.Blocks;
import xreliquary.Reliquary;
import xreliquary.items.XRItems;
import xreliquary.util.AlkahestRecipe;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AlkahestryCraftingRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack tomb = null;
		ItemStack itemStack = null;
		boolean isCharging = false;
		int valid = 0;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(XRItems.alkahestryTome))) {
					tomb = stack.copy();
				} else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
					if (valid == 0) {
						valid = 1;
						itemStack = stack;
					} else {
						valid = 2;
					}
				} else {
					isCharging = true;
				}
			}
		}
		if (tomb != null && valid == 1 && !isCharging && itemStack != null) {
			AlkahestRecipe recipe = null;
			if (Alkahestry.getDictionaryKey(itemStack) == null)
				recipe = Alkahestry.getRegistry().get(ObjectUtils.getItemIdentifier(itemStack.getItem()));
			else
				recipe = Alkahestry.getDictionaryKey(itemStack);
			return (tomb.getItemDamage() + recipe.cost <= Reliquary.PROXY.tombRedstoneLimit);
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
				if (!(ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(XRItems.alkahestryTome)))) {
					if (Alkahestry.getDictionaryKey(stack) == null)
						returned = Alkahestry.getRegistry().get(ObjectUtils.getItemIdentifier(stack.getItem()));
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
		return new ItemStack(XRItems.alkahestryTome, 1);
	}

}
