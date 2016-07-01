package xreliquary.items.alkahestry;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Settings;
import xreliquary.util.CraftingHelper;
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
				if(!CraftingHelper.hasSlotCrafting(inv))
					return false;

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
			ItemStack remainingStack = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);

			if(remainingStack != null && remainingStack.getItem() instanceof ItemAlkahestryTome) {
				NBTHelper.setInteger("charge", remainingStack, NBTHelper.getInteger("charge", remainingStack) - (Settings.AlkahestryTome.baseItemWorth * getCraftingResult(inv).stackSize));
				remainingStack.setItemDamage(remainingStack.getMaxDamage() - NBTHelper.getInteger("charge", remainingStack));
			}

			aitemstack[i] = remainingStack;
		}

		return aitemstack;
	}

}
