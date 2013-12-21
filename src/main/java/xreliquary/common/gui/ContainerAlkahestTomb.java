package xreliquary.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerAlkahestTomb extends Container {
	
	public ContainerAlkahestTomb() {
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
