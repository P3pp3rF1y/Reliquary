package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.alkahestry.AlkahestChargeRecipe;

import javax.annotation.Nonnull;
import java.util.Map;

public class AlkahestryChargingRecipe implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		ItemStack tome = null;
		int amount = 0;
		int valid = 0;

		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.alkahestryTome) {
					tome = stack.copy();
				} else {
					boolean isChargingItem = false;
					for(Map.Entry<String, AlkahestChargeRecipe> entry : Settings.AlkahestryTome.chargingRecipes.entrySet()) {
						AlkahestChargeRecipe recipe = entry.getValue();
						if(stack.getItem() == recipe.item.getItem() && stack.getMetadata() == recipe.item.getMetadata()) {
							if(valid == 0)
								valid = 1;
							amount += recipe.charge;
							isChargingItem = true;
							break;
						}
					}

					if(!isChargingItem) {
						valid = 2;
					}
				}
			}
		}
		return tome != null && valid == 1 && NBTHelper.getInteger("charge", tome) + amount <= Settings.AlkahestryTome.chargeLimit;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack tome = null;
		int amount = 0;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.alkahestryTome) {
					tome = stack.copy();
				} else {
					for(Map.Entry<String, AlkahestChargeRecipe> entry : Settings.AlkahestryTome.chargingRecipes.entrySet()) {
						AlkahestChargeRecipe recipe = entry.getValue();
						if(stack.getItem() == recipe.item.getItem() && stack.getMetadata() == recipe.item.getMetadata()) {
							amount += recipe.charge;
							break;
						}
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
