package xreliquary.util.pedestal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.util.LogHelper;

import java.util.HashMap;
import java.util.Map;

public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();

	private Map<Class<? extends Item>, Class<? extends IPedestalActionItemWrapper>> itemWrappers = new HashMap<>();

	public static void registerItemWrapper(Class<? extends Item> itemClass, Class<? extends IPedestalActionItemWrapper> wrapperClass) {
		INSTANCE.itemWrappers.put(itemClass, wrapperClass);
	}

	public static IPedestalActionItemWrapper getItemWrapper(ItemStack item) {
		for (Class<? extends Item> itemClass : INSTANCE.itemWrappers.keySet()) {
			if (itemClass.isInstance(item.getItem()))
				try {
					return INSTANCE.itemWrappers.get(itemClass).newInstance();
				}
				catch(InstantiationException|IllegalAccessException e) {
					LogHelper.error("Error instantiating pedestal action item wrapper for " + itemClass.getName());
				}
		}
		return null;
	}
}
