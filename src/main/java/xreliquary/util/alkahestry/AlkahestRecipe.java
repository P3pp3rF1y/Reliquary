package xreliquary.util.alkahestry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AlkahestRecipe {

	public ItemStack item = null;
	public int yield = 0;
	public int cost = 0;

	public String dictionaryName = null;

	public AlkahestRecipe(ItemStack par1, int par2, int par3) {
		this.item = par1;
		this.yield = par2;
		this.cost = par3;
	}

	public AlkahestRecipe(String par1, int par2, int par3) {
		this.dictionaryName = par1;
		this.yield = par2;
		this.cost = par3;
	}

}
