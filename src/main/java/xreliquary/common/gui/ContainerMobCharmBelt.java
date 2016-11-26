package xreliquary.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xreliquary.init.ModItems;
import xreliquary.reference.Reference;

import javax.annotation.Nonnull;

public class ContainerMobCharmBelt extends Container {
	private ItemStack belt;
	private static final int PLAYER_INV_INDEX = Reference.MOB_CHARM.COUNT_TYPES + 1;

	@Override
	@Nonnull
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		Slot slot = null;
		if(slotId >= 0 && slotId < this.inventorySlots.size())
			slot = this.inventorySlots.get(slotId);
		ItemStack slotStack = slot == null ? ItemStack.EMPTY : slot.getStack();

		//prevent moving belt out of its slot
		if(slot != null && !slotStack.isEmpty() && slotStack.getItem() == ModItems.mobCharmBelt && slotStack == player.getHeldItemMainhand())
			return ItemStack.EMPTY;

		//overriden here so that on shift click it doesn't retry and thus move more charms out of belt
		if(slotId >= 0 && slotId < PLAYER_INV_INDEX && clickTypeIn == ClickType.QUICK_MOVE && (dragType == 0 || dragType == 1)) {
			if(slotId < 0) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack = ItemStack.EMPTY;

			if(slot != null && slot.canTakeStack(player)) {
				if(!slotStack.isEmpty()) {
					itemstack = slotStack.copy();
				}

				ItemStack transferredStack = this.transferStackInSlot(player, slotId);

				if(!transferredStack.isEmpty()) {
					itemstack = transferredStack.copy();
				}
			}
			return itemstack;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack copiedStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if(slot != null && slot.getHasStack()) {
			ItemStack originalStack = slot.getStack();
			//noinspection ConstantConditions
			copiedStack = originalStack.copy();

			if(index < PLAYER_INV_INDEX) {
				if(!this.mergeItemStack(originalStack, PLAYER_INV_INDEX, PLAYER_INV_INDEX + 36, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(originalStack, copiedStack);
			} else if(index >= PLAYER_INV_INDEX && index < PLAYER_INV_INDEX + 36) {
				if(originalStack.getItem() == ModItems.mobCharm) {
					if(!this.mergeItemStack(originalStack, 0, PLAYER_INV_INDEX, false)) {
						return ItemStack.EMPTY;
					}
				} else if(index >= PLAYER_INV_INDEX && index < PLAYER_INV_INDEX + 27) {
					if(!this.mergeItemStack(originalStack, PLAYER_INV_INDEX + 27, PLAYER_INV_INDEX + 36, false)) {
						return ItemStack.EMPTY;
					}
				} else if(index >= PLAYER_INV_INDEX + 27 && index < PLAYER_INV_INDEX + 36 && !this.mergeItemStack(originalStack, PLAYER_INV_INDEX, PLAYER_INV_INDEX + 27, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (originalStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if(originalStack.getCount() == copiedStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, originalStack);
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
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
		return true;
	}

	@Nonnull
	public ItemStack getBelt() {
		return belt;
	}
}
