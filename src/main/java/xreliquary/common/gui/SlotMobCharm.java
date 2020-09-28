package xreliquary.common.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;

class SlotMobCharm extends Slot {
	private static final IInventory emptyInventory = new Inventory();
	private final ItemStack belt;

	SlotMobCharm(ItemStack belt, int index) {
		super(emptyInventory, index, -999, 0);

		this.belt = belt;
	}


	@Override
	public ItemStack getStack() {
		return ModItems.MOB_CHARM_BELT.getMobCharmInSlot(belt, getSlotIndex());
	}

	@Override
	public void putStack( ItemStack stack) {
		ModItems.MOB_CHARM_BELT.putMobCharmInSlot(belt, getSlotIndex(), stack);
	}

	@Override
	public void onSlotChange(ItemStack newStack, ItemStack originalStack) {
		//noop
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}


	@Override
	public ItemStack decrStackSize(int amount) {
		if(amount > 1) {
			return ItemStack.EMPTY;
		}

		ItemStack mobCharm = ModItems.MOB_CHARM_BELT.getMobCharmInSlot(belt, getSlotIndex());

		ModItems.MOB_CHARM_BELT.removeMobCharmInSlot(belt, getSlotIndex());

		return mobCharm;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return stack.getItem() == ModItems.MOB_CHARM;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof SlotMobCharm && ((SlotMobCharm) other).getBelt() == belt;
	}

	private ItemStack getBelt() {
		return belt;
	}
}
