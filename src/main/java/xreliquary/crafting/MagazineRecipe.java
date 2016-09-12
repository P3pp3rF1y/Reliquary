package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;

public class MagazineRecipe implements IRecipe {
	private static final ItemStack[] EMPTY_ITEMS = new ItemStack[9];

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		if(inv.getWidth() != 3 || inv.getHeight() != 3)
			return false;

		int bulletMeta = -1;
		NBTTagCompound bulletTag = null;
		boolean hasMagazine = false;

		for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);

			if(stack == null)
				return false;

			if(stack.getItem() == ModItems.bullet) {
				if(stack.getMetadata() < 1)
					return false;

				if(bulletMeta == -1) {
					bulletMeta = stack.getMetadata();
					bulletTag = stack.getTagCompound();
				} else if(bulletMeta != stack.getMetadata() || (bulletTag != null && !bulletTag.equals(stack.getTagCompound()))) {
					return false;
				}
			} else if(stack.getItem() == ModItems.magazine) {
				if(stack.getMetadata() != 0 || hasMagazine)
					return false;

				hasMagazine = true;
			} else {
				return false;
			}
		}

		//only bullets and up to 1 magazine are allowed so just returning whether it has magazine is enough here
		return hasMagazine;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack bullet = null;

		//in the first two slots there must be a bullet so no need to iterate further
		for(int slot = 0; slot < 2; slot++) {
			ItemStack stack = inv.getStackInSlot(slot);

			if (stack.getItem()==ModItems.bullet) {
				bullet = stack.copy();
				break;
			}
		}

		if (bullet == null)
			return null;

		ItemStack magazine = new ItemStack(ModItems.magazine, 1, bullet.getMetadata());
		PotionUtils.appendEffects(magazine, PotionUtils.getEffectsFromStack(bullet));

		return magazine;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Nullable
	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return EMPTY_ITEMS;
	}
}
