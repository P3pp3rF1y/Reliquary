package xreliquary.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.NBTHelper;
import xreliquary.util.RegistryHelper;
import xreliquary.util.alkahestry.AlkahestCraftRecipe;
import xreliquary.util.alkahestry.Alkahestry;

import javax.annotation.Nonnull;

public class AlkahestryCraftingRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	public AlkahestryCraftingRecipe() {
		setRegistryName(Reference.MOD_ID, "alkahestry_crafting");
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
		ItemStack tome = ItemStack.EMPTY;
		ItemStack itemStack = ItemStack.EMPTY;
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
		if(!tome.isEmpty() && valid == 1) {
			AlkahestCraftRecipe recipe = Alkahestry.matchCraftRecipe(itemStack);

			return recipe != null && (NBTHelper.getInteger("charge", tome) - recipe.cost >= 0);
		} else {
			return false;
		}
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		AlkahestCraftRecipe returned;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(!(RegistryHelper.getItemRegistryName(stack.getItem()).equals(RegistryHelper.getItemRegistryName(ModItems.alkahestryTome)))) {
					returned = Alkahestry.matchCraftRecipe(stack);

					ItemStack resultStack = stack.copy();
					//noinspection ConstantConditions
					resultStack.setCount(returned.yield + 1);

					return resultStack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 2 && height >= 2;
	}

	private int getCraftingResultCost(IInventory inv) {
		AlkahestCraftRecipe returned = null;
		for(int count = 0; count < inv.getSizeInventory(); count++) {
			ItemStack stack = inv.getStackInSlot(count);
			if(!stack.isEmpty()) {
				if(!(RegistryHelper.getItemRegistryName(stack.getItem()).equals(RegistryHelper.getItemRegistryName(ModItems.alkahestryTome)))) {
					returned = Alkahestry.matchCraftRecipe(stack);
				}
			}
		}
		if(returned == null)
			return 0;
		return returned.cost;
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

	@Override
	public boolean isHidden() {
		return true;
	}
}
