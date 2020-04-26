package xreliquary.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

@SuppressWarnings("SameParameterValue")
public class NBTHelper {
	private NBTHelper() {}

	public static short getShort(String s, ItemStack stack) {
		CompoundNBT tagCompound = getTag(stack);
		return tagCompound.getShort(s);
	}

	public static void putShort(String s, ItemStack stack, short i) {
		CompoundNBT tagCompound = getTag(stack);
		tagCompound.putShort(s, i);
		stack.setTag(tagCompound);
	}

	public static int getInt(String s, ItemStack stack) {
		CompoundNBT tagCompound = getTag(stack);
		return tagCompound.getInt(s);
	}

	public static void putInt(String s, ItemStack stack, int i) {
		CompoundNBT tagCompound = getTag(stack);
		tagCompound.putInt(s, i);
		stack.setTag(tagCompound);
	}

	public static long getLong(String s, ItemStack stack) {
		CompoundNBT tagCompound = getTag(stack);
		return tagCompound.getLong(s);
	}

	public static void putLong(String s, ItemStack stack, long i) {
		CompoundNBT tagCompound = getTag(stack);
		tagCompound.putLong(s, i);
		stack.setTag(tagCompound);
	}

	public static boolean getBoolean(String s, ItemStack stack) {
		CompoundNBT tagCompound = getTag(stack);
		return tagCompound.getBoolean(s);
	}

	public static void putBoolean(String s, ItemStack stack, boolean b) {
		CompoundNBT tagCompound = getTag(stack);
		tagCompound.putBoolean(s, b);
		stack.setTag(tagCompound);
	}

	public static String getString(String s, ItemStack stack) {
		CompoundNBT tagCompound = getTag(stack);
		return tagCompound.getString(s);

	}

	public static void putString(String s, ItemStack stack, String s1) {
		CompoundNBT tagCompound = getTag(stack);
		tagCompound.putString(s, s1);
		stack.setTag(tagCompound);
	}

	public static CompoundNBT getTag(ItemStack stack) {
		if (stack.getTag() == null) {
			return new CompoundNBT();
		}
		return stack.getTag();
	}

	public static void putTagCompound(String s, ItemStack stack, CompoundNBT tc) {
		CompoundNBT tagCompound = getTag(stack);
		tagCompound.put(s, tc);
		stack.setTag(tagCompound);
	}

	public static CompoundNBT getTagCompound(String s, ItemStack stack) {
		CompoundNBT tagCompound = getTag(stack);
		return tagCompound.getCompound(s);
	}

	public static void remove(@Nullable CompoundNBT nbt, String tagName) {
		if (nbt != null) {
			nbt.remove(tagName);
		}
	}

	public static void removeContainedStacks(ItemStack container) {
		remove(container.getTag(), "Slots");
	}

	public static void updateContainedStack(ItemStack container, short slot, ItemStack stackToSave, int count) {
		updateContainedStack(container, slot, stackToSave, count, false);
	}

	public static void updateContainedStack(ItemStack container, short slot, ItemStack stackToSave, int count, boolean updateCountOnly) {
		CompoundNBT tag = getTag(container);

		ListNBT slots = tag.getList("Slots", Constants.NBT.TAG_COMPOUND);

		while (slot > slots.size()) {
			slots.add(getEmptyStackNBT());
		}

		CompoundNBT slotTag;
		if (slot == slots.size()) {
			if (updateCountOnly) {
				return;
			}
			slotTag = new CompoundNBT();
			slots.add(slotTag);
		} else {
			slotTag = (CompoundNBT) slots.get(slot);
		}

		if (!updateCountOnly) {
			slotTag.put("Stack", stackToSave.write(new CompoundNBT()));
		}
		slotTag.putInt("Count", count);
		slots.set(slot, slotTag);

		tag.put("Slots", slots);
		container.setTag(tag);
	}

	public static int getContainedStackCount(ItemStack container, int slot) {
		CompoundNBT tag = getTag(container);

		ListNBT slots = tag.getList("Slots", Constants.NBT.TAG_COMPOUND);

		if (slot < slots.size()) {
			CompoundNBT slotTag = (CompoundNBT) slots.get(slot);
			return slotTag.getInt("Count");
		}

		return 0;
	}

	public static int getCountContainedStacks(ItemStack container) {
		CompoundNBT tag = getTag(container);

		ListNBT slots = tag.getList("Slots", Constants.NBT.TAG_COMPOUND);

		return slots.size();
	}

	public static ItemStack getContainedStack(ItemStack container, int slot) {
		CompoundNBT tag = getTag(container);

		ListNBT slots = tag.getList("Slots", Constants.NBT.TAG_COMPOUND);

		if (slot < slots.size()) {
			CompoundNBT slotTag = (CompoundNBT) slots.get(slot);
			ItemStack ret = ItemStack.read(slotTag.getCompound("Stack"));
			ret.setCount(slotTag.getInt("Count"));
			return ret;
		}
		return ItemStack.EMPTY;
	}

	private static INBT getEmptyStackNBT() {
		CompoundNBT slotTag = new CompoundNBT();
		slotTag.put("Stack", ItemStack.EMPTY.write(new CompoundNBT()));
		slotTag.putInt("Count", 0);

		return slotTag;
	}
}
