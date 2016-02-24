package xreliquary.common.gui;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import xreliquary.client.gui.GuiAlkahestTome;

public class GUIHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0)
            return new ContainerAlkahestTome();
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0)
            return new GuiAlkahestTome(new ContainerAlkahestTome());
        return null;
    }

}
