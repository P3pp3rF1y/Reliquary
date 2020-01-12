package xreliquary.common.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;

class SlotMobCharm extends Slot {
	private static IInventory emptyInventory = new Inventory();
	private ItemStack belt;

	SlotMobCharm(ItemStack belt, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);

		this.belt = belt;
	}


	@Override
	public ItemStack getStack() {
		return ModItems.MOB_CHARM_BELT.getMobCharmInSlot(belt, this.getSlotIndex());
	}

	@Override
	public void putStack( ItemStack stack) {
		ModItems.MOB_CHARM_BELT.putMobCharmInSlot(belt, this.getSlotIndex(), stack);
	}

	@Override
	public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}


	@Override
	public ItemStack decrStackSize(int amount) {
		if(amount > 1)
			return ItemStack.EMPTY;

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
		return other instanceof SlotMobCharm && ((SlotMobCharm) other).getBelt() == this.belt;
	}

	private ItemStack getBelt() {
		return belt;
	}
}
