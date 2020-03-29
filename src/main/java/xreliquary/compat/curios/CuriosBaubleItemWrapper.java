package xreliquary.compat.curios;

import net.minecraft.entity.LivingEntity;
import top.theillusivec4.curios.api.capability.ICurio;
import xreliquary.items.IBaubleItem;

class CuriosBaubleItemWrapper implements ICurio {
	private IBaubleItem baubleItem;

	CuriosBaubleItemWrapper(IBaubleItem baubleItem) {
		this.baubleItem = baubleItem;
	}

	@Override
	public void onEquipped(String identifier, LivingEntity livingEntity) {
		baubleItem.onEquipped(identifier, livingEntity);
	}

	@Override
	public void onCurioTick(String identifier, int index, LivingEntity livingEntity) {
		CuriosCompat.getStackInSlot(livingEntity, identifier, index).ifPresent(stack -> baubleItem.onWornTick(stack, livingEntity));
	}

	@Override
	public boolean canEquip(String identifier, LivingEntity livingEntity) {
		return baubleItem.getBaubleType().getIdentifier().equals(identifier);
	}
}
