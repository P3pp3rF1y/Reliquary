package xreliquary.init;

import net.minecraftforge.fml.ModList;
import xreliquary.compat.ICompat;
import xreliquary.compat.botania.BotaniaCompat;
import xreliquary.compat.curios.CuriosCompat;
import xreliquary.compat.tconstruct.TConstructCompat;
import xreliquary.compat.waila.WailaCompat;
import xreliquary.reference.Compatibility;
import xreliquary.util.LogHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ModCompat {
	private ModCompat() {}

	private static final Map<String, Supplier<Callable<ICompat>>> compatFactories = new HashMap<>();
	static {
		compatFactories.put(Compatibility.ModIds.WAILA, () -> WailaCompat::new);
		compatFactories.put(Compatibility.ModIds.HWYLA, () -> WailaCompat::new);
		compatFactories.put(Compatibility.ModIds.CURIOS, () -> CuriosCompat::new);
		compatFactories.put(Compatibility.ModIds.BOTANIA, () -> BotaniaCompat::new);
		compatFactories.put(Compatibility.ModIds.TINKERS_CONSTRUCT, () -> TConstructCompat::new);
	}

	private static final Set<ICompat> loadedCompats = new HashSet<>();

	public static void initCompats() {
		for(Map.Entry<String, Supplier<Callable<ICompat>>> entry : compatFactories.entrySet()) {
			if (ModList.get().isLoaded(entry.getKey())) {
				try {
					loadedCompats.add(entry.getValue().get().call());
				}
				catch (Exception e) {
					LogHelper.error("Error instantiating compatibility ", e);
				}
			}
		}
	}

	public static void setupCompats() {
		loadedCompats.forEach(ICompat::setup);
	}
}
