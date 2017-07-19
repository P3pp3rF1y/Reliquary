package xreliquary.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;

public class PotionBulletsRecipe implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		int bulletMeta = -1;

		if(inv.getWidth() == 3 && inv.getHeight() == 3) {
			for(int i = 0; i < inv.getWidth(); ++i) {
				for(int j = 0; j < inv.getHeight(); ++j) {
					ItemStack itemstack = inv.getStackInRowAndColumn(i, j);

					if(itemstack.isEmpty()) {
						return false;
					}

					Item item = itemstack.getItem();

					if(i == 1 && j == 1) {
						if(item != ModItems.potion || !ModItems.potion.getLingering(itemstack)) {
							return false;
						}
					} else {
						if(item != ModItems.bullet || itemstack.getMetadata() < 1)
							return false;

						if(bulletMeta == -1) {
							bulletMeta = itemstack.getMetadata();
						} else if(itemstack.getMetadata() != bulletMeta) {
							return false;
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack potion = inv.getStackInRowAndColumn(1, 1);
		ItemStack bullet = inv.getStackInRowAndColumn(0, 0);

		if(!potion.isEmpty() && potion.getItem() == ModItems.potion && ModItems.potion.getLingering(potion)) {
			ItemStack potionBullets = new ItemStack(ModItems.bullet, 8, bullet.getMetadata());
			XRPotionHelper.addPotionEffectsToStack(potionBullets, XRPotionHelper.changePotionEffectsDuration(XRPotionHelper.getPotionEffectsFromStack(potion), 0.2F));
			return potionBullets;
		} else {
			return ItemStack.EMPTY;
		}
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
