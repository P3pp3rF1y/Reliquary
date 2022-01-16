package xreliquary.items.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IPotionItem {
	List<MobEffectInstance> getEffects(ItemStack stack);
}
