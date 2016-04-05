package xreliquary.util.pedestal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import xreliquary.api.IPedestalActionItemWrapper;
import xreliquary.util.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedestalRegistry {
	private static final PedestalRegistry INSTANCE = new PedestalRegistry();
	private static final Map<Long, BlockPos> positions = new HashMap<>();

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

	public static void registerPosition(BlockPos pos) {
		if (!positions.containsKey(pos.toLong()))
			positions.put(pos.toLong(), pos);
	}

	public static void unregisterPosition(BlockPos pos) {
		positions.remove(pos.toLong());
	}
}
