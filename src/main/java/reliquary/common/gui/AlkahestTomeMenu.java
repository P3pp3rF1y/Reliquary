package reliquary.common.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import static reliquary.init.ModItems.ALKAHEST_TOME_MENU_TYPE;

public class AlkahestTomeMenu extends AbstractContainerMenu {
	public AlkahestTomeMenu(int windowId) {
		super(ALKAHEST_TOME_MENU_TYPE.get(), windowId);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return slots.get(index).getItem();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public static AlkahestTomeMenu fromBuffer(int windowId) {
		return new AlkahestTomeMenu(windowId);
	}
}
