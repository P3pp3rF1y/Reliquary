package xreliquary.util.pedestal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.api.IPedestalActionItemWrapper;

import java.util.HashMap;
import java.util.Map;

public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();

	private Map<Class<? extends Item>, IPedestalActionItemWrapper> itemWrappers = new HashMap<>();

	public static void registerItemWrapper(Class<? extends Item> itemClass, IPedestalActionItemWrapper wrapperClass) {
		INSTANCE.itemWrappers.put(itemClass, wrapperClass);
	}

	public static IPedestalActionItemWrapper getItemWrapper(ItemStack item) {
		for (Class<? extends Item> itemClass : INSTANCE.itemWrappers.keySet()) {
			if (itemClass.isInstance(item.getItem()))
				return INSTANCE.itemWrappers.get(itemClass);
		}
		return null;
	}
}
