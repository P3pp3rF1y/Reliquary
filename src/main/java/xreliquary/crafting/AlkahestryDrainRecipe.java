package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.items.ItemAlkahestryTome;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;

import javax.annotation.Nonnull;

public class AlkahestryDrainRecipe implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		boolean valid = false;
		boolean foundNonTome = false;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack.isEmpty())
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

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack tome = ItemStack.EMPTY;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ItemAlkahestryTome) {
					tome = stack;
				}
			}
		}

		if(!tome.isEmpty()) {
			int quantity = NBTHelper.getInteger("charge", tome);

			quantity = quantity / Settings.AlkahestryTome.baseItemWorth;

			quantity = Math.min(quantity, Settings.AlkahestryTome.baseItem.getMaxStackSize());

			if(quantity == 0)
				return ItemStack.EMPTY;
			ItemStack stackToReturn = Settings.AlkahestryTome.baseItem.copy();
			stackToReturn.setCount(quantity);
			return stackToReturn;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for(int slot = 0; slot < remaining.size(); ++slot) {
			ItemStack itemstack = inv.getStackInSlot(slot);
			if(!itemstack.isEmpty() && itemstack.getItem() instanceof ItemAlkahestryTome) {
				ItemStack tomeCopy = itemstack.copy();
				NBTHelper.setInteger("charge", tomeCopy, NBTHelper.getInteger("charge", tomeCopy) - (Settings.AlkahestryTome.baseItemWorth * getCraftingResult(inv).getCount()));
				tomeCopy.setItemDamage(tomeCopy.getMaxDamage() - NBTHelper.getInteger("charge", tomeCopy));
			}
		}

		return remaining;
	}

}
