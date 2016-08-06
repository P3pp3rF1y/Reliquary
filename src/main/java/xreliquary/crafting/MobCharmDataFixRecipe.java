package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.init.XRRecipes;

import javax.annotation.Nullable;

public class MobCharmDataFixRecipe implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean heartZhuFound = false;
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && heartZhuFound)
				return false;

			if (stack != null && stack.getItem() == ModItems.heartZhu) {
				heartZhuFound = true;
			}
		}

		return heartZhuFound;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getItem() == ModItems.heartZhu) {
				return XRRecipes.mobCharm((byte) stack.getMetadata());
			}
		}
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 1;
	}

	@Nullable
	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return new ItemStack[inv.getSizeInventory()];
	}
}
