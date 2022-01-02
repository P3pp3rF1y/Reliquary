package xreliquary.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import xreliquary.reference.Settings;

import javax.annotation.Nullable;
import java.util.List;

public class MobDropItem extends ItemBase {
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (Boolean.TRUE.equals(Settings.COMMON.mobDropsEnabled.get())) {
			super.addInformation(stack, world, tooltip, flag);
		}
	}
}
