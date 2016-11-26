package xreliquary.client.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xreliquary.api.client.IPedestalItemRenderer;
import xreliquary.util.LogHelper;

import java.util.HashMap;
import java.util.Map;

public class PedestalClientRegistry {
	private static final PedestalClientRegistry INSTANCE = new PedestalClientRegistry();

	private Map<Class<? extends Item>, Class<? extends IPedestalItemRenderer>> itemRenderers = new HashMap<>();

	private PedestalClientRegistry() {
	}

	public static void registerItemRenderer(Class<? extends Item> itemClass, Class<? extends IPedestalItemRenderer> rendererClass) {
		INSTANCE.itemRenderers.put(itemClass, rendererClass);
	}

	public static IPedestalItemRenderer getItemRenderer(ItemStack item) {
		for(Class<? extends Item> itemClass : INSTANCE.itemRenderers.keySet()) {
			if(itemClass.isInstance(item.getItem()))
				try {
					return INSTANCE.itemRenderers.get(itemClass).newInstance();
				}
				catch(InstantiationException | IllegalAccessException e) {
					LogHelper.error("Error instantiating pedestal item renderer for " + itemClass.getName());
				}

		}
		return null;
	}

}
