package xreliquary.compat.jei.descriptions;

import net.minecraft.item.ItemStack;

import java.util.List;

public class DescriptionEntry {
	private final String lang;
	private final List<ItemStack> itemStacks;

	public DescriptionEntry(List<ItemStack> itemStack, String langKey) {

		this.itemStacks = itemStack;
		this.lang = langKey;
	}

	public String langKey() {
		return lang;
	}

	public List<ItemStack> itemStacks() {
		return itemStacks;
	}
}
