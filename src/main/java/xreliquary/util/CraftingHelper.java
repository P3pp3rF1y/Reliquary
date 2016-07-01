package xreliquary.util;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;

import java.lang.reflect.Field;

public class CraftingHelper {
	public static boolean hasSlotCrafting(InventoryCrafting inv) {
		Container container = null;

		try {
			Field eventHandlerField = inv.getClass().getDeclaredField("eventHandler");
			eventHandlerField.setAccessible(true);
			container = (Container) eventHandlerField.get(inv);
		}
		catch(NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}

		if (container == null)
			return false;

		if (container.inventorySlots.size()==0 || !(container.inventorySlots.get(0) instanceof SlotCrafting))
			return false;
		return true;
	}
}
