package xreliquary.compat.jei.descriptions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

import java.util.*;
import java.util.stream.Collectors;

public class JEIDescriptionRegistry {
	private static Map<String, List<ItemStack>> registry = new HashMap<>();

	public static Set<DescriptionEntry> entrySet() {
		return registry.entrySet().stream().map(entry -> new DescriptionEntry(entry.getValue(), entry.getKey())).collect(Collectors.toCollection(HashSet::new));
	}

	public static void register(List<ItemStack> itemStacks, String name) {
		registry.put(Reference.MOD_ID + "." + Names.Configs.JEI_DESCRIPTIONS + name, itemStacks);
	}

	public static void register(Item item, String name) {
		if(item.getCreativeTab() != null) {
			if(item.getHasSubtypes()) {
				NonNullList<ItemStack> subItems = NonNullList.create();
				item.getSubItems(item.getCreativeTab(), subItems);

				Set<Integer> addedMeta = new HashSet<>();

				for(ItemStack stack : subItems) {
					if (!addedMeta.contains(stack.getMetadata())) {
						registry.put(Reference.MOD_ID + "." + Names.Configs.JEI_DESCRIPTIONS + name + stack.getMetadata(), Collections.singletonList(stack));
						addedMeta.add(stack.getMetadata());
					} else {
						//expecting meta subitems to be first in subitems collection, the ones different in nbt need to be handled differently
						break;
					}
				}
			} else {
				registry.put(Reference.MOD_ID + "." + Names.Configs.JEI_DESCRIPTIONS + name, Collections.singletonList(new ItemStack(item, 1)));
			}
		}
	}
}
