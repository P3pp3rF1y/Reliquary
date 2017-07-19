package xreliquary.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;

public class XRTippedArrowsRecipe implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
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
	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack itemstack = inv.getStackInRowAndColumn(1, 1);

		if(!itemstack.isEmpty() && itemstack.getItem() == ModItems.potion && ModItems.potion.getLingering(itemstack)) {
			ItemStack tippedArrows = new ItemStack(ModItems.tippedArrow, 8);
			XRPotionHelper.addPotionEffectsToStack(tippedArrows, XRPotionHelper.changePotionEffectsDuration(XRPotionHelper.getPotionEffectsFromStack(itemstack), 0.125F));
			return tippedArrows;
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * Returns the size of the recipe area
	 */
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
