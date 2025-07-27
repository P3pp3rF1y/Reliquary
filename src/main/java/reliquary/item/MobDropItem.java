package reliquary.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import reliquary.reference.Config;

import java.util.List;

public class MobDropItem extends ItemBase {
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (Boolean.TRUE.equals(Config.COMMON.mobDropsEnabled.get())) {
			super.appendHoverText(stack, context, tooltip, flag);
		}
	}
}
