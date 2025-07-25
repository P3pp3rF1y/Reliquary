package reliquary.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import reliquary.init.ModItems;
import reliquary.item.FortuneCoinToggler;
import reliquary.network.FortuneCoinTogglePressedPayload;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicBoolean;

class CuriosFortuneCoinToggler extends FortuneCoinToggler {
	@Override
	public boolean findAndToggle() {
		if (super.findAndToggle()) {
			return true;
		}
		return CuriosApi.getCuriosInventory(Minecraft.getInstance().player).map(handler -> {
			AtomicBoolean result = new AtomicBoolean(false);
			handler.getCurios().forEach((identifier, stackHandler) -> {
				for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
					ItemStack baubleStack = stackHandler.getStacks().getStackInSlot(slot);

					if (baubleStack.getItem() == ModItems.FORTUNE_COIN.get()) {
						ModItems.FORTUNE_COIN.get().toggle(baubleStack);
						stackHandler.getStacks().setStackInSlot(slot, baubleStack);
						PacketDistributor.sendToServer(new FortuneCoinTogglePressedPayload(FortuneCoinTogglePressedPayload.InventoryType.CURIOS, slot, identifier));
						result.set(true);
						return;
					}
				}
			});
			return result.get();
		}).orElse(false);
	}

	public void registerSelf() {
		FortuneCoinToggler.setCoinToggler(this);
	}
}
