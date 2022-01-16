package xreliquary.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import xreliquary.items.util.IHarvestRodCache;

public class ModCapabilities {
	private ModCapabilities() {}

	public static final Capability<IHarvestRodCache> HARVEST_ROD_CACHE = CapabilityManager.get(new CapabilityToken<>() {});

	public static void registerListeners(IEventBus modBus) {
		modBus.addListener(ModCapabilities::onRegister);
	}

	public static void onRegister(RegisterCapabilitiesEvent event) {
		event.register(IHarvestRodCache.class);
	}
}
