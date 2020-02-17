package xreliquary.compat.jei.descriptions;

import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class DescriptionEntry {
	private final String[] lang;
	private final Supplier<List<ItemStack>> supplyItemStacks;

	DescriptionEntry(Supplier<List<ItemStack>> supplyItemStacks, String[] langKeys) {
		this.supplyItemStacks = supplyItemStacks;
		lang = langKeys;
	}

	public String[] langKeys() {
		return lang;
	}

	public List<ItemStack> getItemStacks() {
		return supplyItemStacks.get();
	}
}
