package xreliquary.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;
import xreliquary.util.potions.XRPotionHelper;

import javax.annotation.Nonnull;

public class XRTippedArrowsRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	public XRTippedArrowsRecipe() {
		setRegistryName(Reference.MOD_ID, "tipped_arrows");
	}

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
