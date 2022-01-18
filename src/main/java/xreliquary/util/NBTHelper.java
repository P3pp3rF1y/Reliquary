package xreliquary.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class NBTHelper {

	private static final String SLOTS_TAG = "Slots";
	private static final String STACK_TAG = "Stack";
	private static final String COUNT_TAG = "Count";

	private NBTHelper() {}

	public static short getShort(String s, ItemStack stack) {
		CompoundTag tagCompound = getTag(stack);
		return tagCompound.getShort(s);
	}

	public static void putShort(String s, ItemStack stack, short i) {
		CompoundTag tagCompound = getTag(stack);
		tagCompound.putShort(s, i);
		stack.setTag(tagCompound);
	}

	public static int getInt(String s, ItemStack stack) {
		CompoundTag tagCompound = getTag(stack);
		return tagCompound.getInt(s);
	}

	public static void putInt(String s, ItemStack stack, int i) {
		CompoundTag tagCompound = getTag(stack);
		tagCompound.putInt(s, i);
		stack.setTag(tagCompound);
	}

	public static long getLong(String s, ItemStack stack) {
		CompoundTag tagCompound = getTag(stack);
		return tagCompound.getLong(s);
	}

	public static void putLong(String s, ItemStack stack, long i) {
		CompoundTag tagCompound = getTag(stack);
		tagCompound.putLong(s, i);
		stack.setTag(tagCompound);
	}

	public static boolean getBoolean(String s, ItemStack stack) {
		CompoundTag tagCompound = getTag(stack);
		return tagCompound.getBoolean(s);
	}

	public static void putBoolean(String s, ItemStack stack, boolean b) {
		CompoundTag tagCompound = getTag(stack);
		tagCompound.putBoolean(s, b);
		stack.setTag(tagCompound);
	}

	public static String getString(String s, ItemStack stack) {
		CompoundTag tagCompound = getTag(stack);
		return tagCompound.getString(s);

	}

	public static void putString(String s, ItemStack stack, String s1) {
		CompoundTag tagCompound = getTag(stack);
		tagCompound.putString(s, s1);
		stack.setTag(tagCompound);
	}

	public static CompoundTag getTag(ItemStack stack) {
		if (stack.getTag() == null) {
			return new CompoundTag();
		}
		return stack.getTag();
	}

	public static void putTagCompound(String s, ItemStack stack, CompoundTag tc) {
		CompoundTag tagCompound = getTag(stack);
		tagCompound.put(s, tc);
		stack.setTag(tagCompound);
	}

	public static CompoundTag getTagCompound(String s, ItemStack stack) {
		CompoundTag tagCompound = getTag(stack);
		return tagCompound.getCompound(s);
	}

	public static void remove(@Nullable CompoundTag nbt, String tagName) {
		if (nbt != null) {
			nbt.remove(tagName);
		}
	}

	public static void removeContainedStacks(ItemStack container) {
		remove(container.getTag(), SLOTS_TAG);
	}

	public static void updateContainedStack(ItemStack container, short slot, ItemStack stackToSave, int count) {
		updateContainedStack(container, slot, stackToSave, count, false);
	}

	public static void updateContainedStack(ItemStack container, short slot, ItemStack stackToSave, int count, boolean updateCountOnly) {
		CompoundTag tag = getTag(container);

		ListTag slots = tag.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		while (slot > slots.size()) {
			slots.add(getEmptyStackNBT());
		}

		CompoundTag slotTag;
		if (slot == slots.size()) {
			if (updateCountOnly) {
				return;
			}
			slotTag = new CompoundTag();
			slots.add(slotTag);
		} else {
			slotTag = (CompoundTag) slots.get(slot);
		}

		if (!updateCountOnly) {
			slotTag.put(STACK_TAG, stackToSave.save(new CompoundTag()));
		}
		slotTag.putInt(COUNT_TAG, count);
		slots.set(slot, slotTag);

		tag.put(SLOTS_TAG, slots);
		container.setTag(tag);
	}

	public static int getContainedStackCount(ItemStack container, int slot) {
		CompoundTag tag = getTag(container);

		ListTag slots = tag.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		if (slot < slots.size()) {
			CompoundTag slotTag = (CompoundTag) slots.get(slot);
			return slotTag.getInt(COUNT_TAG);
		}

		return 0;
	}

	public static int getCountContainedStacks(ItemStack container) {
		CompoundTag tag = getTag(container);

		ListTag slots = tag.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		return slots.size();
	}

	public static ItemStack getContainedStack(ItemStack container, int slot) {
		CompoundTag tag = getTag(container);

		ListTag slots = tag.getList(SLOTS_TAG, Tag.TAG_COMPOUND);

		if (slot < slots.size()) {
			CompoundTag slotTag = (CompoundTag) slots.get(slot);
			ItemStack ret = ItemStack.of(slotTag.getCompound(STACK_TAG));
			ret.setCount(slotTag.getInt(COUNT_TAG));
			return ret;
		}
		return ItemStack.EMPTY;
	}

	private static Tag getEmptyStackNBT() {
		CompoundTag slotTag = new CompoundTag();
		slotTag.put(STACK_TAG, ItemStack.EMPTY.save(new CompoundTag()));
		slotTag.putInt(COUNT_TAG, 0);

		return slotTag;
	}

	public static <T extends Enum<T>> Optional<T> getEnumConstant(ItemStack stack, String key, Function<String, T> deserialize) {
		return getTagValue(stack, key, (t, k) -> deserialize.apply(t.getString(k)));
	}

	private static <T> Optional<T> getTagValue(ItemStack stack, String key, BiFunction<CompoundTag, String, T> getValue) {
		return getTagValue(stack, "", key, getValue);
	}

	public static <T> Optional<T> getTagValue(ItemStack stack, String parentKey, String key, BiFunction<CompoundTag, String, T> getValue) {
		CompoundTag tag = stack.getTag();

		if (tag == null) {
			return Optional.empty();
		}

		if (!parentKey.isEmpty()) {
			Tag parentTag = tag.get(parentKey);
			if (!(parentTag instanceof CompoundTag)) {
				return Optional.empty();
			}
			tag = (CompoundTag) parentTag;
		}

		return getTagValue(tag, key, getValue);
	}

	private static <T> Optional<T> getTagValue(CompoundTag tag, String key, BiFunction<CompoundTag, String, T> getValue) {
		if (!tag.contains(key)) {
			return Optional.empty();
		}

		return Optional.of(getValue.apply(tag, key));
	}

	public static Optional<Integer> getInt(ItemStack stack, String key) {
		return getTagValue(stack, key, CompoundTag::getInt);
	}
}
