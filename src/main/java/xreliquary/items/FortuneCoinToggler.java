package xreliquary.items;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import xreliquary.client.ClientProxy;
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
		if (ClientProxy.FORTUNE_COIN_TOGGLE_KEYBIND.isPressed()) {
			coinToggler.findAndToggle();
		}
	}

	public boolean findAndToggle() {
		PlayerEntity player = Minecraft.getInstance().player;
		for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
			ItemStack stack = player.inventory.mainInventory.get(slot);
			if (stack.getItem() == ModItems.FORTUNE_COIN) {
				PacketHandler.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.MAIN, slot));

				ModItems.FORTUNE_COIN.toggle(stack);
				return true;
			}
		}
		if (player.inventory.offHandInventory.get(0).getItem() == ModItems.FORTUNE_COIN) {
			PacketHandler.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.OFF_HAND, 0));
			ModItems.FORTUNE_COIN.toggle(player.inventory.offHandInventory.get(0));
			return true;
		}
		return false;
	}
}
