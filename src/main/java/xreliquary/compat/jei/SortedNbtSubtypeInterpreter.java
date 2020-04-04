package xreliquary.compat.jei;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class SortedNbtSubtypeInterpreter implements ISubtypeInterpreter {
	private Set<String> keysToConsider;
	SortedNbtSubtypeInterpreter(String... keysToConsider) {
		this.keysToConsider = new HashSet<>(Arrays.asList(keysToConsider));
	}
	@Override
	public String apply(ItemStack itemStack) {
		CompoundNBT nbtTagCompound = itemStack.getTag();
		return nbtTagCompound != null && !nbtTagCompound.isEmpty() ? getSortedCompoundString(nbtTagCompound, k -> keysToConsider.contains(k)) : "";
	}

	private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");

	private String handleEscape(String keyName) {
		return SIMPLE_VALUE.matcher(keyName).matches() ? keyName : StringNBT.quoteAndEscape(keyName);
	}

	private String getSortedCompoundString(CompoundNBT nbtTagCompound, Predicate<String> addKey) {
		StringBuilder stringbuilder = new StringBuilder("{");
		List<String> sortedKeys = nbtTagCompound.keySet().stream().filter(addKey).sorted().collect(Collectors.toList());
		for(String s : sortedKeys) {
			if (stringbuilder.length() != 1) {
				stringbuilder.append(',');
			}

			INBT nbtValue = nbtTagCompound.get(s);
			//noinspection ConstantConditions - only iterating through keys that exist so nbtValue can't be null
			stringbuilder.append(handleEscape(s)).append(':').append(getNbtString(nbtValue));
		}

		return stringbuilder.append('}').toString();
	}

	private String getNbtString(INBT nbtValue) {
		if (nbtValue instanceof ListNBT) {
			return getNbtListString((ListNBT) nbtValue);
		} else if (nbtValue instanceof CompoundNBT) {
			return getSortedCompoundString((CompoundNBT) nbtValue, k -> true);
		} else {
			return nbtValue.toString();
		}
	}

	private String getNbtListString(ListNBT listNBT) {
		StringBuilder stringbuilder = new StringBuilder("[");

		for(int i = 0; i < listNBT.size(); ++i) {
			if (i != 0) {
				stringbuilder.append(',');
			}

			stringbuilder.append(getNbtString(listNBT.get(i)));
		}

		return stringbuilder.append(']').toString();
	}
}
