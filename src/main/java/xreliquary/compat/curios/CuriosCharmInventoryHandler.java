package xreliquary.compat.curios;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.inventory.CurioStackHandler;
import xreliquary.init.ModItems;
import xreliquary.items.IBaubleItem;
import xreliquary.items.MobCharmDefinition;
import xreliquary.items.MobCharmItem;

public class CuriosCharmInventoryHandler extends MobCharmItem.CharmInventoryHandler {
	@Override
	public boolean playerHasMobCharm(PlayerEntity player, MobCharmDefinition charmDefinition) {
		if (super.playerHasMobCharm(player, charmDefinition)) {
			return true;
		}
		return CuriosAPI.getCuriosHandler(player).map(handler -> {
			CurioStackHandler stackHandler = handler.getStackHandler(IBaubleItem.Type.BELT.getIdentifier());
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStackInSlot(slot);
				if (!baubleStack.isEmpty() && baubleStack.getItem() == ModItems.MOB_CHARM_BELT && ModItems.MOB_CHARM_BELT.hasCharm(baubleStack, charmDefinition.getRegistryName())) {
					return true;
				}
			}
			return false;
		}).orElse(false);
	}

	@Override
	public boolean damagePlayersMobCharm(PlayerEntity player, String entityRegistryName) {
		if (super.damagePlayersMobCharm(player, entityRegistryName)) {
			return true;
		}
		return CuriosAPI.getCuriosHandler(player).map(handler -> {
			CurioStackHandler stackHandler = handler.getStackHandler(IBaubleItem.Type.BELT.getIdentifier());
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStackInSlot(slot);

				if (baubleStack.isEmpty()) {
					continue;
				}

				if (damageMobCharmInBelt((ServerPlayerEntity) player, entityRegistryName, baubleStack)) {
					return true;
				}
			}
			return false;
		}).orElse(false);
	}
}
