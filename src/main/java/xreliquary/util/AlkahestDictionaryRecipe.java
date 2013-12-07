package xreliquary.util;

import net.minecraft.item.ItemStack;

public class AlkahestDictionaryRecipe extends AlkahestRecipe {
	
	public static String dictionaryName = null;

	public AlkahestDictionaryRecipe(String par1, int par2, int par3) {
		super(null, par2, par3);
		this.dictionaryName = par1;
	}

}
