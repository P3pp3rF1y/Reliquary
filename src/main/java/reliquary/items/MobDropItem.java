package reliquary.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import reliquary.reference.Settings;

import javax.annotation.Nullable;
import java.util.List;

public class MobDropItem extends ItemBase {
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		if (Boolean.TRUE.equals(Settings.COMMON.mobDropsEnabled.get())) {
			super.appendHoverText(stack, world, tooltip, flag);
		}
	}
}
