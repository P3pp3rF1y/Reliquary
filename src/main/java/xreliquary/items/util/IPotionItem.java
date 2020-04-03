package xreliquary.items.util;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;

import java.util.List;

public interface IPotionItem {
	List<EffectInstance> getEffects(ItemStack stack);
}
