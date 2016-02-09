package xreliquary.compat.jei.descriptions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import xreliquary.reference.Compatibility;
import xreliquary.reference.Names;

import java.util.*;


public class JEIDescriptionRegistry
{
	private static Map<String, List<ItemStack>> registry = new HashMap<>();

	public static Set<DescriptionEntry> entrySet() {
		HashSet<DescriptionEntry> descriptionEntries = new HashSet<>();

		for ( Map.Entry<String, List<ItemStack>> entry : registry.entrySet()) {
			descriptionEntries.add(new DescriptionEntry(entry.getValue(), entry.getKey()));
		}

		return descriptionEntries;
	}

	@Optional.Method(modid= Compatibility.MOD_ID.JEI)
	public static void register(List<ItemStack> itemStacks, String name) {
		registry.put(Names.jei_description_prefix + name, itemStacks);
	}

	@Optional.Method(modid= Compatibility.MOD_ID.JEI)
	public static void register(Item item, String name) {
		if (item.getCreativeTab() != null) {
			if (item.getHasSubtypes()) {
				ArrayList<ItemStack> subItems = new ArrayList<>();
				item.getSubItems(item, item.getCreativeTab(), subItems);

				for(ItemStack stack : subItems) {
					registry.put(Names.jei_description_prefix + name + stack.getMetadata(), Collections.singletonList(stack));
				}
			} else {
				registry.put(Names.jei_description_prefix + name,Collections.singletonList(new ItemStack(item, 1)));
			}
		}
	}
}
