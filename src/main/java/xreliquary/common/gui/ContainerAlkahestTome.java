package xreliquary.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

class ContainerAlkahestTome extends Container {

	public ContainerAlkahestTome() {
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
