package xreliquary.compat.jei.descriptions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.reference.Reference;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class JEIDescriptionRegistry {
	private JEIDescriptionRegistry() {}

	private static final Set<DescriptionEntry> descriptionEntries = new HashSet<>();

	public static Set<DescriptionEntry> getEntries() {
		return descriptionEntries;
	}

	public static void register(Supplier<List<ItemStack>> itemStacks, String... names) {
		descriptionEntries.add(new DescriptionEntry(itemStacks, getTranslationKeys(names)));
	}

	private static String[] getTranslationKeys(String... names) {
		String[] keys = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			keys[i] = String.format("jei.%s.description.%s", Reference.MOD_ID, names[i].replace('/', '.'));
		}

		return keys;
	}

	public static void register(ItemStack stack, String name) {
		register(() -> Collections.singletonList(stack), name);
	}

	public static void register(Item item, String name) {
		if (item.getGroup() != null) {
			register(new ItemStack(item), name);
		}
	}
}
