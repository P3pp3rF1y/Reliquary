package xreliquary.compat.curios;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import xreliquary.init.ModItems;
import xreliquary.items.MobCharmDefinition;
import xreliquary.items.MobCharmItem;
import xreliquary.items.util.IBaubleItem;

public class CuriosCharmInventoryHandler extends MobCharmItem.CharmInventoryHandler {
	@Override
	public boolean playerHasMobCharm(PlayerEntity player, MobCharmDefinition charmDefinition) {
		if (super.playerHasMobCharm(player, charmDefinition)) {
			return true;
		}
		return CuriosApi.getCuriosHelper().getCuriosHandler(player).map(handler -> handler.getStacksHandler(IBaubleItem.Type.BELT.getIdentifier()).map(stackHandler -> {
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
	public boolean damagePlayersMobCharm(PlayerEntity player, String entityRegistryName) {
		if (super.damagePlayersMobCharm(player, entityRegistryName)) {
			return true;
		}
		return CuriosApi.getCuriosHelper().getCuriosHandler(player).map(handler -> handler.getStacksHandler(IBaubleItem.Type.BELT.getIdentifier()).map(stackHandler -> {
			for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
				ItemStack baubleStack = stackHandler.getStacks().getStackInSlot(slot);

				if (baubleStack.isEmpty()) {
					continue;
				}

				if (damageMobCharmInBelt((ServerPlayerEntity) player, entityRegistryName, baubleStack)) {
					return true;
				}
			}
			return false;
		}).orElse(false)).orElse(false);
	}
}
