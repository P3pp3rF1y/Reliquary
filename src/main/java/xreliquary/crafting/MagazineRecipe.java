package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;

public class MagazineRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	public MagazineRecipe() {
		setRegistryName(Reference.MOD_ID, "magazine");
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		if(inv.getWidth() != 3 || inv.getHeight() != 3)
			return false;

		int bulletMeta = -1;
		NBTTagCompound bulletTag = null;
		boolean hasMagazine = false;

		for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
			ItemStack stack = inv.getStackInSlot(slot);

			if(stack.isEmpty())
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

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack bullet = ItemStack.EMPTY;

		//in the first two slots there must be a bullet so no need to iterate further
		for(int slot = 0; slot < 2; slot++) {
			ItemStack stack = inv.getStackInSlot(slot);

			if(stack.getItem() == ModItems.bullet) {
				bullet = stack.copy();
				break;
			}
		}

		if(bullet.isEmpty())
			return ItemStack.EMPTY;

		ItemStack magazine = new ItemStack(ModItems.magazine, 1, bullet.getMetadata());
		XRPotionHelper.addPotionEffectsToStack(magazine, XRPotionHelper.getPotionEffectsFromStack(bullet));

		return magazine;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 3 && height >= 3;
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

	@Override
	public boolean isHidden() {
		return true;
	}
}
