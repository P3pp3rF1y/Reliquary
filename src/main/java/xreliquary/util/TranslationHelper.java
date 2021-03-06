package xreliquary.util;

import net.minecraft.item.Item;
import xreliquary.reference.Reference;

public class TranslationHelper {
	private TranslationHelper() {}

	private static final String ITEM_PREFIX = "item." + Reference.MOD_ID + ".";

	public static String transl(Item item) {
		return ITEM_PREFIX + RegistryHelper.getRegistryName(item).getPath().replace('/', '_');
	}

	public static String translTooltip(Item item) {
		return transl(item) + ".tooltip";
	}
}
