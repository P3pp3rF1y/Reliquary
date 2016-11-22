package xreliquary.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.Nonnull;

class ContainerAlkahestTome extends Container {

	ContainerAlkahestTome() {
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player) {
		return true;
	}

}
