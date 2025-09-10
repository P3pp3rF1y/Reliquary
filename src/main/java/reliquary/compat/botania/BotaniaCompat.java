package reliquary.compat.botania;

import reliquary.item.FortuneCoinItem;
import vazkii.botania.api.BotaniaAPI;

public class BotaniaCompat {
	public BotaniaCompat() {
		FortuneCoinItem.addFortuneCoinPickupChecker(itemEntity -> !BotaniaAPI.instance().hasSolegnoliaAround(itemEntity));
	}
}
