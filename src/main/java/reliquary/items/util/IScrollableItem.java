package reliquary.items.util;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IScrollableItem {
	InteractionResult onMouseScrolled(ItemStack stack, Player player, double scrollDelta);
}
