package xreliquary.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import xreliquary.items.util.ICuriosItem;

class CuriosBaubleItemWrapper implements ICurio {
	private final ItemStack baubleStack;
	private final ICuriosItem curiosItem;

	CuriosBaubleItemWrapper(ItemStack baubleStack) {
		this.baubleStack = baubleStack;
		curiosItem = (ICuriosItem) baubleStack.getItem();
	}

	@Override
	public ItemStack getStack() {
		return baubleStack;
	}

	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack) {
		curiosItem.onEquipped(slotContext.getIdentifier(), slotContext.getWearer());
	}

	@Override
	public void curioTick(String identifier, int index, LivingEntity livingEntity) {
		CuriosCompat.getStackInSlot(livingEntity, identifier, index).ifPresent(stack -> curiosItem.onWornTick(stack, livingEntity));
	}

	@Override
	public boolean canEquip(String identifier, LivingEntity livingEntity) {
		return curiosItem.getCuriosType().getIdentifier().equals(identifier);
	}
}
