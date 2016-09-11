package xreliquary.crafting;

import net.minecraft.init.Items;
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

public class XRTippedArrowsRecipe implements IRecipe {
	private static final ItemStack[] EMPTY_ITEMS = new ItemStack[9];

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting inv, World worldIn) {
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
					} else if(item != Items.ARROW) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Nullable
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack itemstack = inv.getStackInRowAndColumn(1, 1);

		if(itemstack != null && itemstack.getItem() == ModItems.potion && ModItems.potion.getLingering(itemstack)) {
			ItemStack tippedArrows = new ItemStack(ModItems.tippedArrow, 8);
			PotionUtils.addPotionToItemStack(tippedArrows, PotionTypes.EMPTY);
			PotionUtils.appendEffects(tippedArrows, XRPotionHelper.changeDuration(new PotionEssence(itemstack.getTagCompound()).getEffects(), 0.125F));
			return tippedArrows;
		} else {
			return null;
		}
	}

	/**
	 * Returns the size of the recipe area
	 */
	public int getRecipeSize() {
		return 9;
	}

	@Nullable
	public ItemStack getRecipeOutput() {
		return null;
	}

	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return EMPTY_ITEMS;
	}
}
