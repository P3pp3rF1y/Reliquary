package xreliquary.items.util;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ILeftClickableItem {
	InteractionResult onLeftClickItem(ItemStack stack, LivingEntity entity);
}
