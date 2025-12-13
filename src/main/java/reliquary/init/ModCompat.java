package reliquary.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import reliquary.compat.accessories.AccessoriesCompat;
import reliquary.compat.botania.BotaniaCompat;
import reliquary.compat.curios.CuriosCompat;
import reliquary.compat.tconstruct.TConstructCompat;
import reliquary.reference.Compatibility;
import reliquary.util.LogHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModCompat {
    private ModCompat() {
    }

    private static final Map<String, Supplier<Consumer<IEventBus>>> compatFactories = new HashMap<>();

    static {
        compatFactories.put(Compatibility.ModIds.CURIOS, () -> CuriosCompat::new);
        compatFactories.put(Compatibility.ModIds.BOTANIA, () -> BotaniaCompat::new);
        compatFactories.put(Compatibility.ModIds.TINKERS_CONSTRUCT, () -> TConstructCompat::new);
        compatFactories.put(Compatibility.ModIds.ACCESSORIES, () -> AccessoriesCompat::new);
    }

    public static void initCompats(IEventBus modBus) {
        for (Map.Entry<String, Supplier<Consumer<IEventBus>>> entry : compatFactories.entrySet()) {
            if (ModList.get().isLoaded(entry.getKey())) {
                try {
                    entry.getValue().get().accept(modBus);
                } catch (Exception e) {
                    LogHelper.error("Error instantiating compatibility ", e);
                }
            }
        }
    }
}
