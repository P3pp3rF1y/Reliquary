package reliquary.item.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public interface IPotionItem {
	PotionContents getPotionContents(ItemStack stack);
}
