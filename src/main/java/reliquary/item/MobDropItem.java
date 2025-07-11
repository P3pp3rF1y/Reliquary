package reliquary.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import reliquary.reference.Config;

import java.util.function.Consumer;

public class MobDropItem extends ItemBase {
	public MobDropItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
		if (Boolean.TRUE.equals(Config.COMMON.mobDropsEnabled.get())) {
			super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
		}
	}
}
