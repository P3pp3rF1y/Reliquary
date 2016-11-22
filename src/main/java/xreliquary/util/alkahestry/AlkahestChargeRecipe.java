package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class AlkahestChargeRecipe {
	public ItemStack item = ItemStack.EMPTY;
	public int charge = 0;

	public String dictionaryName = null;

	public AlkahestChargeRecipe(@Nonnull ItemStack item, int charge) {
		this.item = item;
		this.charge = charge;
	}
}
