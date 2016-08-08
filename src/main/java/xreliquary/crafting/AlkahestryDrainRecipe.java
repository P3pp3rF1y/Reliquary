package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;

public class AlkahestryDrainRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean valid = false;
		boolean foundNonTome = false;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack == null)
				continue;
			if((stack.getItem() instanceof ItemAlkahestryTome)) {
				if(!valid)
					valid = NBTHelper.getInteger("charge", stack) > 0;
			} else {
				foundNonTome = true;
			}
		}
		return !foundNonTome && valid;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack tome = null;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(stack.getItem() instanceof ItemAlkahestryTome) {
					tome = stack;
				}
			}
		}

		if(tome != null) {
			int quantity = NBTHelper.getInteger("charge", tome);

			quantity = quantity / Settings.AlkahestryTome.baseItemWorth;

			quantity = Math.min(quantity, Settings.AlkahestryTome.baseItem.getMaxStackSize());

			if(quantity == 0)
				return null;
			ItemStack stackToReturn = Settings.AlkahestryTome.baseItem.copy();
			stackToReturn.stackSize = quantity;
			return stackToReturn;
		}
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ModItems.alkahestryTome, 1);
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

		for(int i = 0; i < aitemstack.length; ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			ItemStack stackCopy = itemstack == null ? null : itemstack.copy();

			if(stackCopy != null && stackCopy.getItem() instanceof ItemAlkahestryTome) {
				NBTHelper.setInteger("charge", stackCopy, NBTHelper.getInteger("charge", stackCopy) - (Settings.AlkahestryTome.baseItemWorth * getCraftingResult(inv).stackSize));
				stackCopy.setItemDamage(stackCopy.getMaxDamage() - NBTHelper.getInteger("charge", stackCopy));
			} else {
				stackCopy = null;
			}

			aitemstack[i] = stackCopy;
		}

		return aitemstack;
	}

}
