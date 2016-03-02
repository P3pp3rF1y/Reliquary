package xreliquary.compat.waila;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import xreliquary.compat.ICompat;
import xreliquary.reference.Compatibility;

public class WailaCompat implements ICompat {
    @Override
    public void loadCompatibility(InitializationPhase phase, World world) {
        if (phase == InitializationPhase.INIT)
            FMLInterModComms.sendMessage(getModId(), "register", "xreliquary.compat.waila.WailaCallbackHandler.callbackRegister");
    }

    @Override
    public String getModId() {
        return Compatibility.MOD_ID.WAILA;
    }
}
