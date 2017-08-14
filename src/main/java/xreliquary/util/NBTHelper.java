package xreliquary.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

@SuppressWarnings("SameParameterValue")
public class NBTHelper {

	public static short getShort(String s, ItemStack ist) {
		NBTTagCompound tagCompound = getTag(ist);
		return tagCompound.getShort(s);
	}

	public static void setShort(String s, ItemStack ist, short i) {
		NBTTagCompound tagCompound = getTag(ist);
		tagCompound.setShort(s, i);
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
			return new NBTTagCompound();
		return ist.getTagCompound();
	}

	public static void setTag(ItemStack ist, NBTTagCompound nbt) {
		ist.setTagCompound(nbt);
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

	public static void updateContainedStack(ItemStack container, short slot, ItemStack stackToSave, int count) {
		updateContainedStack(container, slot, stackToSave, count, false);
	}

	public static void updateContainedStack(ItemStack container, short slot, ItemStack stackToSave, int count, boolean updateCountOnly) {
		NBTTagCompound tag = getTag(container);

		NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

		while(slot > slots.tagCount()) {
			slots.appendTag(getEmptyStackNBT());
		}

		NBTTagCompound slotTag;
		if(slot == slots.tagCount()) {
			if(updateCountOnly) {
				return;
			}
			slotTag = new NBTTagCompound();
			slots.appendTag(slotTag);
		} else {
			slotTag = (NBTTagCompound) slots.get(slot);
		}

		if(!updateCountOnly) {
			slotTag.setTag("Stack", stackToSave.writeToNBT(new NBTTagCompound()));
		}
		slotTag.setInteger("Count", count);
		slots.set(slot, slotTag);

		tag.setTag("Slots", slots);
		container.setTagCompound(tag);
	}

	public static int getContainedStackCount(ItemStack container, int slot) {
		NBTTagCompound tag = getTag(container);

		NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

		if (slot < slots.tagCount()) {
			NBTTagCompound slotTag = (NBTTagCompound) slots.get(slot);
			return slotTag.getInteger("Count");
		}

		return 0;
	}
	public static int getCountContainedStacks(ItemStack container) {
		NBTTagCompound tag = getTag(container);

		NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

		return slots.tagCount();
	}

	public static ItemStack getContainedStack(ItemStack container, int slot) {
		NBTTagCompound tag = getTag(container);

		NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

		if (slot < slots.tagCount()) {
			NBTTagCompound slotTag = (NBTTagCompound) slots.get(slot);
			ItemStack ret = new ItemStack((NBTTagCompound) slotTag.getTag("Stack"));
			ret.setCount(slotTag.getInteger("Count"));
			return ret;
		}
		return ItemStack.EMPTY;
	}

	private static NBTBase getEmptyStackNBT() {
		NBTTagCompound slotTag = new NBTTagCompound();
		slotTag.setTag("Stack", ItemStack.EMPTY.writeToNBT(new NBTTagCompound()));
		slotTag.setInteger("Count", 0);

		return slotTag;
	}
}
