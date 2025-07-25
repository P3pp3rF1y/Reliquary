package reliquary.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import reliquary.client.gui.AlkahestryTomeScreen;
import reliquary.client.gui.MobCharmBeltScreen;

public class ModItemsClient {

	public static void init(IEventBus modBus) {
		modBus.addListener(ModItemsClient::onMenuScreenRegister);
	}

	private static void onMenuScreenRegister(RegisterMenuScreensEvent event) {
		event.register(ModItems.ALKAHESTRY_TOME_MENU_TYPE.get(), AlkahestryTomeScreen::new);
		event.register(ModItems.MOB_CHAR_BELT_MENU_TYPE.get(), MobCharmBeltScreen::new);
	}
}
