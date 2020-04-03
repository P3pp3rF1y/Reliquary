package xreliquary.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;

public interface ILeftClickableItem {
	ActionResultType onLeftClickItem(ItemStack stack, LivingEntity entity);
}
