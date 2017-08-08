package xreliquary.crafting.factories;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import xreliquary.init.ModItems;

import javax.annotation.Nullable;

public class AlkahestryTomeIngredient extends Ingredient {
	private int chargeNeeded;

	public AlkahestryTomeIngredient(ItemStack tome, int chargeNeeded) {
		super(tome);
		this.chargeNeeded = chargeNeeded;
	}

	public int getChargeNeeded() {
		return chargeNeeded;
	}

	@Override
	public boolean apply(@Nullable ItemStack inventoryStack) {
		return inventoryStack != null && inventoryStack.getItem() == ModItems.alkahestryTome && ModItems.alkahestryTome.getCharge(inventoryStack) >= chargeNeeded;
	}
}
