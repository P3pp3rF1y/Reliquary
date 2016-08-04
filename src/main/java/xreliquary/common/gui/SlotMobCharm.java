package xreliquary.common.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;

import javax.annotation.Nullable;

public class SlotMobCharm extends Slot {
	private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
	private ItemStack belt;

	public SlotMobCharm(ItemStack belt, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);

		this.belt = belt;
	}

	@Nullable
	@Override
	public ItemStack getStack() {
		return ModItems.mobCharmBelt.getMobCharmInSlot(belt, this.getSlotIndex());
	}

	@Override
	public void putStack(@Nullable ItemStack stack) {
		ModItems.mobCharmBelt.putMobCharmInSlot(belt, this.getSlotIndex(), stack);
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
			return null;

		ItemStack mobCharm = ModItems.mobCharmBelt.getMobCharmInSlot(belt, getSlotIndex());

		ModItems.mobCharmBelt.removeMobCharmInSlot(belt, getSlotIndex());

		return mobCharm;
	}

	@Override
	public boolean isItemValid(@Nullable ItemStack stack) {
		return stack.getItem() == ModItems.mobCharm;
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof SlotMobCharm && ((SlotMobCharm) other).getBelt() == this.belt;
	}

	public ItemStack getBelt() {
		return belt;
	}
}
