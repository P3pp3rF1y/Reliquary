package xreliquary.common.gui;

import xreliquary.client.gui.GuiAlkahestTomb;
import xreliquary.items.XRItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == 0)
			return new ContainerAlkahestTomb();
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == 0)
			return new GuiAlkahestTomb(new ContainerAlkahestTomb());
		return null;
	}

}
