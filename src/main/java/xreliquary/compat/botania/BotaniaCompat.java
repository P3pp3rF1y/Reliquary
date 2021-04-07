package xreliquary.compat.botania;

import vazkii.botania.api.BotaniaAPI;
import xreliquary.compat.ICompat;
import xreliquary.items.FortuneCoinItem;

public class BotaniaCompat implements ICompat {
	@Override
	public void setup() {
		FortuneCoinItem.addFortuneCoinPickupChecker(itemEntity -> !BotaniaAPI.instance().hasSolegnoliaAround(itemEntity));
	}
}
