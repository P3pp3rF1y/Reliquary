package reliquary.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.handler.ClientEventHandler;
import reliquary.init.ModItems;
import reliquary.network.FortuneCoinTogglePressedPayload;
import reliquary.util.PlayerInventoryProvider;

public class FortuneCoinToggler {
	private static final FortuneCoinToggler INSTANCE = new FortuneCoinToggler();

	@SuppressWarnings({"squid:S1172", "unused"}) //used in addListener reflection code
	public static void handleKeyInputEvent(ClientTickEvent.Pre event) {
		if (ClientEventHandler.FORTUNE_COIN_TOGGLE_KEYBIND.consumeClick()) {
			INSTANCE.findAndToggle();
		}
	}

	public boolean findAndToggle() {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return false;
		}
		return PlayerInventoryProvider.get().getFromPlayerInventoryHandlers(player,
				(slotStack, handler, handlerName, identifier, slot, ret) -> {
					if (slotStack.getItem() == ModItems.FORTUNE_COIN.get()) {
						PacketDistributor.sendToServer(new FortuneCoinTogglePressedPayload(handlerName, identifier, slot));

						ModItems.FORTUNE_COIN.get().toggle(slotStack);
						handler.setStackInSlot(player, identifier, slot, slotStack);
						return true;
					}
					return false;
				}, ret -> ret, () -> false);
	}
}
