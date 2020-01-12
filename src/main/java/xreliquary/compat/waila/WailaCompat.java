package xreliquary.compat.waila;

import xreliquary.compat.ICompat;
import xreliquary.reference.Compatibility;

public class WailaCompat implements ICompat {
	@Override
	public void loadCompatibility() {
/*  TODO implement waila compatibility
		if (phase == InitializationPhase.INIT)
			FMLInterModComms.sendMessage(getModId(), "register", "xreliquary.compat.waila.WailaCallbackHandler.callbackRegister");
*/
	}

	@Override
	public String getModId() {
		return Compatibility.MOD_ID.WAILA;
	}
}
