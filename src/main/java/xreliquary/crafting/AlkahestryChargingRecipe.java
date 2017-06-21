package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import javax.annotation.Nonnull;

public class AlkahestryChargingRecipe implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		ItemStack tome = ItemStack.EMPTY;
		int amount = 0;

		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.alkahestryTome) {
					tome = stack.copy();
				} else {
					AlkahestChargeRecipe recipe = Alkahestry.matchChargeRecipe(stack);

					if(recipe != null) {
						amount += recipe.charge;
					} else {
						return false;
					}
				}
			}
		}

		return !tome.isEmpty() && NBTHelper.getInteger("charge", tome) + amount <= Settings.AlkahestryTome.chargeLimit;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack tome = ItemStack.EMPTY;
		int amount = 0;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.alkahestryTome) {
					tome = stack.copy();
				} else {
					AlkahestChargeRecipe recipe = Alkahestry.matchChargeRecipe(stack);
					if(recipe != null) {
						amount += recipe.charge;
					}
				}
			}
		}

		NBTHelper.setInteger("charge", tome, NBTHelper.getInteger("charge", tome) + amount);
		//noinspection ConstantConditions
		tome.setItemDamage(tome.getMaxDamage() - NBTHelper.getInteger("charge", tome));
		return tome;
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
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}
}
