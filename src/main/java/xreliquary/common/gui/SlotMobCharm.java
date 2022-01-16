package xreliquary.common.gui;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import xreliquary.init.ModItems;

class SlotMobCharm extends Slot {
	private static final Container emptyInventory = new SimpleContainer();
	private final ItemStack belt;

	SlotMobCharm(ItemStack belt, int index) {
		super(emptyInventory, index, -999, 0);

		this.belt = belt;
	}


	@Override
	public ItemStack getItem() {
		return ModItems.MOB_CHARM_BELT.get().getMobCharmInSlot(belt, getSlotIndex());
	}

	@Override
	public void set( ItemStack stack) {
		ModItems.MOB_CHARM_BELT.get().putMobCharmInSlot(belt, getSlotIndex(), stack);
	}

	@Override
	public void onQuickCraft(ItemStack newStack, ItemStack originalStack) {
		//noop
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}


	@Override
	public ItemStack remove(int amount) {
		if(amount > 1) {
			return ItemStack.EMPTY;
		}

		ItemStack mobCharm = ModItems.MOB_CHARM_BELT.get().getMobCharmInSlot(belt, getSlotIndex());

		ModItems.MOB_CHARM_BELT.get().removeMobCharmInSlot(belt, getSlotIndex());

		return mobCharm;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() == ModItems.MOB_CHARM.get();
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof SlotMobCharm slotMobCharm && slotMobCharm.getBelt() == belt;
	}

	private ItemStack getBelt() {
		return belt;
	}
}
