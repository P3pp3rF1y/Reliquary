package xreliquary.compat.botania;

import xreliquary.compat.ICompat;

public class BotaniaCompat implements ICompat {
	@Override
	public void setup() {
		//TODO readd when botania is ported
		//FortuneCoinItem.addFortuneCoinPickupChecker(itemEntity -> !BotaniaAPI.instance().hasSolegnoliaAround(itemEntity));
	}
}
