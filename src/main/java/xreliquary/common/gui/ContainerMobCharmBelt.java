package xreliquary.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

import javax.annotation.Nullable;

public class ContainerMobCharmBelt extends Container {
	private ItemStack belt;
	private static final int PLAYER_INV_INDEX = Reference.MOB_CHARM.COUNT_TYPES + 1;

	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack copiedStack = null;
		Slot slot = this.inventorySlots.get(index);


		if (slot != null && slot.getHasStack())
		{
			ItemStack originalStack = slot.getStack();
			copiedStack = originalStack.copy();

			if (index < PLAYER_INV_INDEX)
			{
				if (!this.mergeItemStack(originalStack, PLAYER_INV_INDEX, PLAYER_INV_INDEX + 36, true))
				{
					return null;
				}

				slot.onSlotChange(originalStack, copiedStack);
			}
			else if (index >= PLAYER_INV_INDEX && index < PLAYER_INV_INDEX + 36)
			{
				if (originalStack.getItem() == ModItems.mobCharm)
				{
					if (!this.mergeItemStack(originalStack, 0, PLAYER_INV_INDEX, false))
					{
						return null;
					}
				}
				else if (index >= PLAYER_INV_INDEX && index < PLAYER_INV_INDEX + 27)
				{
					if (!this.mergeItemStack(originalStack, PLAYER_INV_INDEX + 27, PLAYER_INV_INDEX + 36, false))
					{
						return null;
					}
				}
				else if (index >= PLAYER_INV_INDEX + 27 && index < PLAYER_INV_INDEX + 36 && !this.mergeItemStack(originalStack, PLAYER_INV_INDEX, PLAYER_INV_INDEX + 27, false))
				{
					return null;
				}
			}

			if (originalStack.stackSize == 0)
			{
				slot.putStack(null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (originalStack.stackSize == copiedStack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(playerIn, originalStack);
		}

		return copiedStack;
	}

	public ContainerMobCharmBelt(InventoryPlayer playerInventory, ItemStack belt) {
		this.belt = belt;

		for(int i = 0; i < Reference.MOB_CHARM.COUNT_TYPES + 1; i++) {
			this.addSlotToContainer(new SlotMobCharm(belt, i, -999, 0));
		}

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 113 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 171));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public ItemStack getBelt() {
		return belt;
	}
}
