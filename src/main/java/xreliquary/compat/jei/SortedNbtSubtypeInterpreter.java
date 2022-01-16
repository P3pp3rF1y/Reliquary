package xreliquary.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

class SortedNbtSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
	private final Set<String> keysToConsider;

	SortedNbtSubtypeInterpreter(String... keysToConsider) {
		this.keysToConsider = new HashSet<>(Arrays.asList(keysToConsider));
	}

	@Override
	public String apply(ItemStack itemStack, UidContext context) {
		CompoundTag nbtTagCompound = itemStack.getTag();
		return nbtTagCompound != null && !nbtTagCompound.isEmpty() ? getSortedCompoundString(nbtTagCompound, keysToConsider::contains) : "";
	}

	private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

	private String handleEscape(String keyName) {
		return SIMPLE_VALUE.matcher(keyName).matches() ? keyName : StringTag.quoteAndEscape(keyName);
	}

	private String getSortedCompoundString(CompoundTag nbtTagCompound, Predicate<String> addKey) {
		StringBuilder stringbuilder = new StringBuilder("{");
		List<String> sortedKeys = nbtTagCompound.getAllKeys().stream().filter(addKey).sorted().toList();
		for (String s : sortedKeys) {
			if (stringbuilder.length() != 1) {
				stringbuilder.append(',');
			}

			Tag nbtValue = nbtTagCompound.get(s);
			//noinspection ConstantConditions - only iterating through keys that exist so nbtValue can't be null
			stringbuilder.append(handleEscape(s)).append(':').append(getNbtString(nbtValue));
		}

		return stringbuilder.append('}').toString();
	}

	private String getNbtString(Tag nbtValue) {
		if (nbtValue instanceof ListTag listTag) {
			return getNbtListString(listTag);
		} else if (nbtValue instanceof CompoundTag compoundTag) {
			return getSortedCompoundString(compoundTag, k -> true);
		} else {
			return nbtValue.toString();
		}
	}

	private String getNbtListString(ListTag listNBT) {
		StringBuilder stringbuilder = new StringBuilder("[");

		for (int i = 0; i < listNBT.size(); ++i) {
			if (i != 0) {
				stringbuilder.append(',');
			}

			stringbuilder.append(getNbtString(listNBT.get(i)));
		}

		return stringbuilder.append(']').toString();
	}
}
