package xreliquary.compat.curios;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import xreliquary.items.util.IBaubleItem;

class CuriosBaubleItemWrapper implements ICurio {
	private final IBaubleItem baubleItem;

	CuriosBaubleItemWrapper(IBaubleItem baubleItem) {
		this.baubleItem = baubleItem;
	}

	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack) {
		baubleItem.onEquipped(slotContext.getIdentifier(), slotContext.getWearer());
	}

	@Override
	public void curioTick(String identifier, int index, LivingEntity livingEntity) {
		CuriosCompat.getStackInSlot(livingEntity, identifier, index).ifPresent(stack -> baubleItem.onWornTick(stack, livingEntity));
	}

	@Override
	public boolean canEquip(String identifier, LivingEntity livingEntity) {
		return baubleItem.getBaubleType().getIdentifier().equals(identifier);
	}
}
