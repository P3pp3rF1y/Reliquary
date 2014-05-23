package xreliquary.lib.alkahestry;

import xreliquary.init.ContentHandler;
import xreliquary.lib.Names;
import xreliquary.util.ObjectUtils;
import xreliquary.Reliquary;
import xreliquary.util.alkahestry.AlkahestRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class AlkahestryCraftingRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack tome = null;
		ItemStack itemStack = null;
		int valid = 0;
		for (int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if (stack != null) {
				if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome)))) {
					tome = stack.copy();
				} else if (!ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome)))) {
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
				recipe = Alkahestry.getRegistry().get(ObjectUtils.getItemIdentifier(itemStack.getItem()));
			else
				recipe = Alkahestry.getDictionaryKey(itemStack);
   			return recipe != null && (tome.getItemDamage() + recipe.cost <= Reliquary.PROXY.tomeRedstoneLimit);
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
				if (!(ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome))))) {
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
		return new ItemStack(ContentHandler.getItem(Names.alkahest_tome), 1);
	}

}
