package reliquary.compat.curios;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.items.MobCharmDefinition;
import reliquary.items.MobCharmItem;
import reliquary.items.util.ICuriosItem;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosCharmInventoryHandler extends MobCharmItem.CharmInventoryHandler {
	@Override
	public boolean playerHasMobCharm(Player player, MobCharmDefinition charmDefinition) {
		if (super.playerHasMobCharm(player, charmDefinition)) {
			return true;
		}
		return CuriosApi.getCuriosHelper().getCuriosHandler(player).map(handler -> handler.getStacksHandler(ICuriosItem.Type.BELT.getIdentifier()).map(stackHandler -> {
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStacks().getStackInSlot(slot);
				if (!baubleStack.isEmpty() && baubleStack.getItem() == ModItems.MOB_CHARM_BELT.get() && ModItems.MOB_CHARM_BELT.get().hasCharm(baubleStack, charmDefinition.getRegistryName())) {
					return true;
				}
			}
			return false;
		}).orElse(false)).orElse(false);
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
