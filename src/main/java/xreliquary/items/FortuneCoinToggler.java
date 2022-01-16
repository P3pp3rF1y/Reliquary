package xreliquary.items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import xreliquary.handler.ClientEventHandler;
import xreliquary.init.ModItems;
import xreliquary.network.PacketFortuneCoinTogglePressed;
import xreliquary.network.PacketHandler;

@OnlyIn(Dist.CLIENT)
public class FortuneCoinToggler {
	@OnlyIn(Dist.CLIENT)
	private static FortuneCoinToggler coinToggler = new FortuneCoinToggler();

	@OnlyIn(Dist.CLIENT)
	public static void setCoinToggler(FortuneCoinToggler toggler) {
		coinToggler = toggler;
	}

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings({"squid:S1172", "unused"}) //used in addListener reflection code
	public static void handleKeyInputEvent(TickEvent.ClientTickEvent event) {
		if (ClientEventHandler.FORTUNE_COIN_TOGGLE_KEYBIND.consumeClick()) {
			coinToggler.findAndToggle();
		}
	}

	public boolean findAndToggle() {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return false;
		}

		for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
			ItemStack stack = player.getInventory().items.get(slot);
			if (stack.getItem() == ModItems.FORTUNE_COIN.get()) {
				PacketHandler.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.MAIN, slot));

				ModItems.FORTUNE_COIN.get().toggle(stack);
				return true;
			}
		}
		if (player.getInventory().offhand.get(0).getItem() == ModItems.FORTUNE_COIN.get()) {
			PacketHandler.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.OFF_HAND, 0));
			ModItems.FORTUNE_COIN.get().toggle(player.getInventory().offhand.get(0));
			return true;
		}
		return false;
	}
}
