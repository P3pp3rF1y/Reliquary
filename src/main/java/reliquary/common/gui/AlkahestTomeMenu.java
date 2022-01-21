package reliquary.common.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import static reliquary.init.ModItems.ALKAHEST_TOME_CONTAINER_TYPE;

public class AlkahestTomeMenu extends AbstractContainerMenu {
	public AlkahestTomeMenu(int windowId) {
		super(ALKAHEST_TOME_CONTAINER_TYPE.get(), windowId);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public static AlkahestTomeMenu fromBuffer(int windowId) {
		return new AlkahestTomeMenu(windowId);
	}
}
