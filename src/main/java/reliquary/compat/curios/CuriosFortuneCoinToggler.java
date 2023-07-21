package reliquary.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import reliquary.init.ModItems;
import reliquary.items.FortuneCoinToggler;
import reliquary.network.PacketFortuneCoinTogglePressed;
import reliquary.network.PacketHandler;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
class CuriosFortuneCoinToggler extends FortuneCoinToggler {
	@Override
	public boolean findAndToggle() {
		if (super.findAndToggle()) {
			return true;
		}
		return CuriosApi.getCuriosHelper().getCuriosHandler(Minecraft.getInstance().player).map(handler -> {
			AtomicBoolean result = new AtomicBoolean(false);
			handler.getCurios().forEach((identifier, stackHandler) -> {
				for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
					ItemStack baubleStack = stackHandler.getStacks().getStackInSlot(slot);

					if (baubleStack.getItem() == ModItems.FORTUNE_COIN.get()) {
						ModItems.FORTUNE_COIN.get().toggle(baubleStack);
						stackHandler.getStacks().setStackInSlot(slot, baubleStack);
						PacketHandler.sendToServer(new PacketFortuneCoinTogglePressed(PacketFortuneCoinTogglePressed.InventoryType.CURIOS, identifier, slot));
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
