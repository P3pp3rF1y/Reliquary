package xreliquary.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.reference.Settings;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import javax.annotation.Nonnull;

public class AlkahestryCraftingRecipe implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		ItemStack tome = null;
		ItemStack itemStack = null;
		int valid = 0;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ModItems.alkahestryTome) {
					tome = stack.copy();
				} else if(stack.getItem() != ModItems.alkahestryTome) {
					if(valid == 0) {
						valid = 1;
						itemStack = stack;
					} else {
						valid = 2;
					}
				}
			}
		}
		if(tome != null && valid == 1) {
			AlkahestCraftRecipe recipe;
			if(Alkahestry.getRecipeByDictionaryKey(itemStack) == null) {
				String key = getItemKey(itemStack);
				recipe = Settings.AlkahestryTome.craftingRecipes.get(key);
			} else
				recipe = Alkahestry.getRecipeByDictionaryKey(itemStack);
			return recipe != null && (NBTHelper.getInteger("charge", tome) - recipe.cost >= 0);
		} else {
			return false;
		}
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		AlkahestCraftRecipe returned = null;
		ItemStack dictStack = ItemStack.EMPTY;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() != ModItems.alkahestryTome) {
					if(Alkahestry.getRecipeByDictionaryKey(stack) == null) {
						String key = getItemKey(stack);
						returned = Settings.AlkahestryTome.craftingRecipes.get(key);
					} else {
						returned = Alkahestry.getRecipeByDictionaryKey(stack);
						dictStack = stack;
					}
				}
			}
		}

		//TODO this dictStack stuff seems wrong, refactor !!
		if(dictStack.isEmpty()) {
			return new ItemStack(returned.item.getItem(), returned.yield + 1, returned.item.getItemDamage());
		} else {
			return new ItemStack(dictStack.getItem(), returned.yield + 1, dictStack.getItemDamage());
		}
	}

	private String getItemKey(ItemStack stack) {

		return RegistryHelper.getItemRegistryName(stack.getItem()) + "|" + stack.getMetadata();
	}

	private int getCraftingResultCost(IInventory inv) {
		AlkahestCraftRecipe returned = null;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(stack.getItem() != ModItems.alkahestryTome) {
					if(Alkahestry.getRecipeByDictionaryKey(stack) == null) {
						String key = getItemKey(stack);
						returned = Settings.AlkahestryTome.craftingRecipes.get(key);
					} else {
						returned = Alkahestry.getRecipeByDictionaryKey(stack);
					}
				}
			}
		}
		if(returned == null)
			return 0;
		return returned.cost;
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
			if(!itemstack.isEmpty() && itemstack.getItem() == ModItems.alkahestryTome) {
				ItemStack tomeCopy = itemstack.copy();
				NBTHelper.setInteger("charge", tomeCopy, NBTHelper.getInteger("charge", tomeCopy) - getCraftingResultCost(inv));
				tomeCopy.setItemDamage(tomeCopy.getMaxDamage() - NBTHelper.getInteger("charge", tomeCopy));
				remaining.set(slot, tomeCopy);
			}
		}

		return remaining;
	}
}
