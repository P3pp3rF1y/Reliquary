package reliquary.compat.curios;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.item.MobCharmItem;
import reliquary.item.util.ICuriosItem;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Set;

public class CuriosCharmInventoryHandler extends MobCharmItem.CharmInventoryHandler {
	@Override
	protected Set<String> getCharmRegistryNames(Player player) {
		Set<String> ret = super.getCharmRegistryNames(player);
		CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve().flatMap(handler -> handler.getStacksHandler(ICuriosItem.Type.BELT.getIdentifier())).ifPresent(stackHandler -> {
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStacks().getStackInSlot(slot);
				if (!baubleStack.isEmpty() && baubleStack.getItem() == ModItems.MOB_CHARM_BELT.get()) {
					ret.addAll(ModItems.MOB_CHARM_BELT.get().getCharmRegistryNames(baubleStack));
				}
			}
		});
		return ret;
	}

	@Override
	public boolean damagePlayersMobCharm(Player player, String entityRegistryName) {
		if (super.damagePlayersMobCharm(player, entityRegistryName)) {
			return true;
		}
		return CuriosApi.getCuriosHelper().getCuriosHandler(player).map(handler -> handler.getStacksHandler(ICuriosItem.Type.BELT.getIdentifier()).map(stackHandler -> {
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStacks().getStackInSlot(slot);

				if (baubleStack.isEmpty()) {
					continue;
				}

				if (damageMobCharmInBelt((ServerPlayer) player, entityRegistryName, baubleStack)) {
					return true;
				}
			}
			return false;
		}).orElse(false)).orElse(false);
	}
}
