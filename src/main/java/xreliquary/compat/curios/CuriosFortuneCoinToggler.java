package xreliquary.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;
import xreliquary.init.ModItems;
import xreliquary.items.FortuneCoinToggler;
import xreliquary.items.util.IBaubleItem;
import xreliquary.network.PacketFortuneCoinTogglePressed;
import xreliquary.network.PacketHandler;

@OnlyIn(Dist.CLIENT)
class CuriosFortuneCoinToggler extends FortuneCoinToggler {
	@Override
	public boolean findAndToggle() {
		if (super.findAndToggle()) {
			return true;
		}
		return CuriosAPI.getCuriosHandler(Minecraft.getInstance().player).map(handler -> {
			CurioStackHandler stackHandler = handler.getStackHandler(IBaubleItem.Type.NECKLACE.getIdentifier());
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStackInSlot(slot);

				if (baubleStack.getItem() == ModItems.FORTUNE_COIN) {
					ModItems.FORTUNE_COIN.toggle(baubleStack);
					stackHandler.setStackInSlot(slot, baubleStack);
					PacketHandler.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.CURIOS, slot));
					return true;
				}
			}
			return false;
		}).orElse(false);
	}

	public void registerSelf() {
		FortuneCoinToggler.setCoinToggler(this);
	}
}
