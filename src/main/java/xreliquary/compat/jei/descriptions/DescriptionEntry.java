package xreliquary.compat.jei.descriptions;

import net.minecraft.item.ItemStack;

public class DescriptionEntry
{
	private final String lang;
	private final ItemStack ist;

	public DescriptionEntry(ItemStack itemStack, String langKey) {

		this.ist = itemStack;
		this.lang = langKey;
	}

	public String langKey()
	{
		return lang;
	}

	public ItemStack itemStack()
	{
		return ist;
	}
}
