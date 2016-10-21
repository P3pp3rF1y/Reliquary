package xreliquary.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
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

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack tome = null;
		ItemStack itemStack = null;
		int valid = 0;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(RegistryHelper.getItemRegistryName(stack.getItem()).equals(RegistryHelper.getItemRegistryName(ModItems.alkahestryTome))) {
					tome = stack.copy();
				} else if(!RegistryHelper.getItemRegistryName(stack.getItem()).equals(RegistryHelper.getItemRegistryName(ModItems.alkahestryTome))) {
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
			if(Alkahestry.getDictionaryKey(itemStack) == null) {
				String key = RegistryHelper.getItemRegistryName(itemStack.getItem()) + "|" + itemStack.getMetadata();
				recipe = Settings.AlkahestryTome.craftingRecipes.get(key);
			} else
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
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(!(RegistryHelper.getItemRegistryName(stack.getItem()).equals(RegistryHelper.getItemRegistryName(ModItems.alkahestryTome)))) {
					if(Alkahestry.getDictionaryKey(stack) == null) {
						String key = RegistryHelper.getItemRegistryName(stack.getItem()) + "|" + stack.getMetadata();
						returned = Settings.AlkahestryTome.craftingRecipes.get(key);
					} else {
						returned = Alkahestry.getDictionaryKey(stack);
						dictStack = stack;
					}
				}
			}
		}

		if(dictStack == null) {
			return new ItemStack(returned.item.getItem(), returned.yield + 1, returned.item.getItemDamage());
		} else {
			return new ItemStack(dictStack.getItem(), returned.yield + 1, dictStack.getItemDamage());
		}
	}

	private int getCraftingResultCost(IInventory inv) {
		AlkahestCraftRecipe returned = null;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(stack != null) {
				if(!(RegistryHelper.getItemRegistryName(stack.getItem()).equals(RegistryHelper.getItemRegistryName(ModItems.alkahestryTome)))) {
					if(Alkahestry.getDictionaryKey(stack) == null) {
						String key = RegistryHelper.getItemRegistryName(stack.getItem()) + (stack.getItem().getHasSubtypes() ? "|" + stack.getMetadata() : "");
						returned = Settings.AlkahestryTome.craftingRecipes.get(key);
					} else {
						returned = Alkahestry.getDictionaryKey(stack);
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
				NBTHelper.setInteger("charge", stackCopy, NBTHelper.getInteger("charge", stackCopy) - getCraftingResultCost(inv));
				stackCopy.setItemDamage(stackCopy.getMaxDamage() - NBTHelper.getInteger("charge", stackCopy));
			} else {
				stackCopy = null;
			}
			aitemstack[i] = stackCopy;
		}

		return aitemstack;
	}
}
