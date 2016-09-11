package xreliquary.crafting;

import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nullable;

public class PotionBulletsRecipe implements IRecipe {
	private static final ItemStack[] EMPTY_ITEMS = new ItemStack[9];

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int bulletMeta = -1;

		if(inv.getWidth() == 3 && inv.getHeight() == 3) {
			for(int i = 0; i < inv.getWidth(); ++i) {
				for(int j = 0; j < inv.getHeight(); ++j) {
					ItemStack itemstack = inv.getStackInRowAndColumn(i, j);

					if(itemstack == null) {
						return false;
					}

					Item item = itemstack.getItem();

					if(i == 1 && j == 1) {
						if(item != ModItems.potion || !ModItems.potion.getLingering(itemstack)) {
							return false;
						}
					} else {
						if (item != ModItems.bullet)
							return false;

						if (bulletMeta == -1) {
							bulletMeta = itemstack.getMetadata();
						} else if (itemstack.getMetadata()!= bulletMeta) {
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

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack potion = inv.getStackInRowAndColumn(1, 1);
		ItemStack bullet = inv.getStackInRowAndColumn(0,0);

		if(potion != null && potion.getItem() == ModItems.potion && ModItems.potion.getLingering(potion)) {
			ItemStack potionBullets = new ItemStack(ModItems.bullet, 8, bullet.getMetadata());
			PotionUtils.appendEffects(potionBullets, XRPotionHelper.changeDuration(new PotionEssence(potion.getTagCompound()).getEffects(), 0.125F));
			return potionBullets;
		} else {
			return null;
		}
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
