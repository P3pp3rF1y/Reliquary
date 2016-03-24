package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;

public class AlkahestCraftRecipe {

	public ItemStack item = null;
	public int yield = 0;
	public int cost = 0;

	public String dictionaryName = null;

	public AlkahestCraftRecipe(ItemStack stack, int yield, int cost) {
		this.item = stack;
		this.yield = yield;
		this.cost = cost;
	}

	public AlkahestCraftRecipe(String dictionaryName, int yield, int cost) {
		this.dictionaryName = dictionaryName;
		this.yield = yield;
		this.cost = cost;
	}

}
