package reliquary.init;

import net.minecraftforge.fml.ModList;
import reliquary.compat.botania.BotaniaCompat;
import reliquary.compat.curios.CuriosCompat;
import reliquary.compat.tconstruct.TConstructCompat;
import reliquary.compat.waila.WailaCompat;
import reliquary.reference.Compatibility;
import reliquary.util.LogHelper;

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
