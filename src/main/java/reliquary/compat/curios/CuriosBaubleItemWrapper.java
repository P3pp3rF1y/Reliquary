package reliquary.compat.curios;

import net.minecraft.world.item.ItemStack;
import reliquary.item.util.ICuriosItem;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

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
		curiosItem.onEquipped(slotContext.identifier(), slotContext.entity());
	}

	@Override
	public void curioTick(SlotContext slotContext) {
		CuriosCompat.getStackInSlot(slotContext.entity(), slotContext.identifier(), slotContext.index()).ifPresent(stack -> curiosItem.onWornTick(stack, slotContext.entity()));
	}

	@Override
	public boolean canEquip(SlotContext slotContext) {
		return curiosItem.getCuriosType().getIdentifier().equals(slotContext.identifier());
	}
}
