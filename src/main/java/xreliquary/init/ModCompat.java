package xreliquary.init;

import net.minecraftforge.fml.ModList;
import xreliquary.compat.botania.BotaniaCompat;
import xreliquary.compat.curios.CuriosCompat;
import xreliquary.compat.tconstruct.TConstructCompat;
import xreliquary.compat.waila.WailaCompat;
import xreliquary.reference.Compatibility;
import xreliquary.util.LogHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModCompat {
	private ModCompat() {}

	private static final Map<String, Supplier<Runnable>> compatFactories = new HashMap<>();
	static {
		compatFactories.put(Compatibility.ModIds.WAILA, () -> WailaCompat::new);
		compatFactories.put(Compatibility.ModIds.HWYLA, () -> WailaCompat::new);
		compatFactories.put(Compatibility.ModIds.CURIOS, () -> CuriosCompat::new);
		compatFactories.put(Compatibility.ModIds.BOTANIA, () -> BotaniaCompat::new);
		compatFactories.put(Compatibility.ModIds.TINKERS_CONSTRUCT, () -> TConstructCompat::new);
	}

	public static void initCompats() {
		for(Map.Entry<String, Supplier<Runnable>> entry : compatFactories.entrySet()) {
			if (ModList.get().isLoaded(entry.getKey())) {
				try {
					entry.getValue().get().run();
				}
				catch (Exception e) {
					LogHelper.error("Error instantiating compatibility ", e);
				}
			}
		}
	}
}
