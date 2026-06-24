package reliquary.compat.botania;

import net.neoforged.bus.api.IEventBus;

public class BotaniaCompat {

	public BotaniaCompat(IEventBus modBus) {
	}
	public void setup() {
		// TODO readd when botania is ported
		// FortuneCoinItem.addFortuneCoinPickupChecker(itemEntity -> !BotaniaAPI.instance().hasSolegnoliaAround(itemEntity));
	}
}
