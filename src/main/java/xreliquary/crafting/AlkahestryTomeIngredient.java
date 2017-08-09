package xreliquary.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import xreliquary.init.ModItems;

import javax.annotation.Nullable;

public class AlkahestryTomeIngredient extends Ingredient {
	private int charge;

	public AlkahestryTomeIngredient(ItemStack tome, int charge) {
		super(tome);
		this.charge = charge;
	}

	@Override
	public boolean apply(@Nullable ItemStack inventoryStack) {
		return inventoryStack != null && inventoryStack.getItem() == ModItems.alkahestryTome && ModItems.alkahestryTome.getCharge(inventoryStack) >= charge;
	}
}
