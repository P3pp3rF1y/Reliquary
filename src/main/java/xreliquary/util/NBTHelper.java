package xreliquary.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public class NBTHelper {

	public static int getShort(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getShort(s);
	}

	public static void setShort(String s, ItemStack ist, int i) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setShort(s, (short) i);
		setTag(ist, tagCompound);
	}

	public static int getInteger(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getInteger(s);
	}

	public static void setInteger(String s, ItemStack ist, int i) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setInteger(s, i);
		setTag(ist, tagCompound);
	}

	public static long getLong(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getLong(s);
	}

	public static void setLong(String s, ItemStack ist, long i) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setLong(s, i);
		setTag(ist, tagCompound);
	}

	public static boolean getBoolean(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getBoolean(s);
	}

	public static void setBoolean(String s, ItemStack ist, boolean b) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setBoolean(s, b);
		setTag(ist, tagCompound);
	}

	public static String getString(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getString(s);

	}

	public static void setString(String s, ItemStack ist, String s1) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setString(s, s1);
		setTag(ist, tagCompound);
	}

	public static NBTTagCompound getTag(ItemStack ist) {
		if(ist.getTagCompound() == null)
			resetTag(ist);
		return ist.getTagCompound();
	}

	public static void setTag(ItemStack ist, NBTTagCompound nbt) {
		ist.setTagCompound(nbt);
	}

	public static void resetTag(ItemStack ist) {
		setTag(ist, new NBTTagCompound());
	}

	public static void setTagCompound(String s, ItemStack ist, NBTTagCompound tc) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setTag(s, tc);
		setTag(ist, tagCompound);
	}

	public static NBTTagCompound getTagCompound(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getCompoundTag(s);
	}

	public static void removeTag(NBTTagCompound nbt, String tagName) {
		if (nbt!= null) {
			nbt.removeTag(tagName);
		}
	}
}
