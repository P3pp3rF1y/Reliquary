package xreliquary.common.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

import static xreliquary.init.ModItems.ALKAHEST_TOME_CONTAINER_TYPE;

public class ContainerAlkahestTome extends Container {
	public ContainerAlkahestTome(int windowId) {
		super(ALKAHEST_TOME_CONTAINER_TYPE.get(), windowId);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public static ContainerAlkahestTome fromBuffer(int windowId) {
		return new ContainerAlkahestTome(windowId);
	}
}
