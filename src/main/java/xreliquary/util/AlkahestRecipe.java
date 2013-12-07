package xreliquary.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AlkahestRecipe {
	
	public static int LOW_TIER = 4;
	public static int MIDDLE_TIER = 8;
	public static int HIGH_TIER = 32;
	public static int UBER_TIER = 64;
	
	public static ItemStack item = null;
	public static int yield = 0;
	public static int cost = 0;
	
	public AlkahestRecipe(ItemStack par1, int par2, int par3) {
		this.item = par1;
		this.yield = par2;
		this.cost = par3;
	}

}
