package xreliquary.compat.jei.descriptions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JEIDescriptionRegistry {
	private JEIDescriptionRegistry() {}

	private static Map<String, List<ItemStack>> registry = new HashMap<>();

	public static Set<DescriptionEntry> entrySet() {
		return registry.entrySet().stream().map(entry -> new DescriptionEntry(entry.getValue(), entry.getKey())).collect(Collectors.toCollection(HashSet::new));
	}

	public static void register(List<ItemStack> itemStacks, String name) {
		registry.put(Reference.MOD_ID + "." + Names.Configs.JEI_DESCRIPTIONS + name, itemStacks);
	}

	public static void register(Item item, String name) {
		if (item.getGroup() != null) {
			registry.put(Reference.MOD_ID + "." + Names.Configs.JEI_DESCRIPTIONS + name, Collections.singletonList(new ItemStack(item, 1)));
		}
	}
}
