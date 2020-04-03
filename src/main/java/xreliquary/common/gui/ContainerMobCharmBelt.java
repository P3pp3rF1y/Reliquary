package xreliquary.common.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.registries.ObjectHolder;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Reference;

public class ContainerMobCharmBelt extends Container {
	@ObjectHolder(Reference.MOD_ID + ":" + Names.Items.MOB_CHARM_BELT)
	public static ContainerType<ContainerMobCharmBelt> TYPE;

	public ContainerMobCharmBelt(int windowId, PlayerInventory playerInventory, ItemStack belt) {
		super(TYPE, windowId);
		this.belt = belt;

		for (int i = 0; i < Reference.MOB_CHARM.COUNT_TYPES + 1; i++) {
			addSlot(new SlotMobCharm(belt, i, -999, 0));
		}

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 113 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			addSlot(new Slot(playerInventory, k, 8 + k * 18, 171));
		}
	}

	private ItemStack belt;

	private static final int PLAYER_INV_INDEX = Reference.MOB_CHARM.COUNT_TYPES + 1;

	@Override

	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		Slot slot = null;
		if (slotId >= 0 && slotId < inventorySlots.size()) {
			slot = inventorySlots.get(slotId);
		}
		ItemStack slotStack = slot == null ? ItemStack.EMPTY : slot.getStack();

		//prevent moving belt out of its slot
		if (slot != null && !slotStack.isEmpty() && slotStack.getItem() == ModItems.MOB_CHARM_BELT && slotStack == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}

		//overriden here so that on shift click it doesn't retry and thus move more charms out of belt
		if (slotId >= 0 && slotId < PLAYER_INV_INDEX && clickTypeIn == ClickType.QUICK_MOVE && (dragType == 0 || dragType == 1)) {
			ItemStack itemstack = ItemStack.EMPTY;

			if (slot != null && slot.canTakeStack(player)) {
				if (!slotStack.isEmpty()) {
					itemstack = slotStack.copy();
				}

				ItemStack transferredStack = transferStackInSlot(player, slotId);

				if (!transferredStack.isEmpty()) {
					itemstack = transferredStack.copy();
				}
			}
			return itemstack;
		}

		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack copiedStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack originalStack = slot.getStack();
			copiedStack = originalStack.copy();

			if (index < PLAYER_INV_INDEX) {
				if (!mergeItemStack(originalStack, PLAYER_INV_INDEX, PLAYER_INV_INDEX + 36, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(originalStack, copiedStack);
			} else if (index < PLAYER_INV_INDEX + 36) {
				if (originalStack.getItem() == ModItems.MOB_CHARM) {
					if (!mergeItemStack(originalStack, 0, PLAYER_INV_INDEX, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < PLAYER_INV_INDEX + 27) {
					if (!mergeItemStack(originalStack, PLAYER_INV_INDEX + 27, PLAYER_INV_INDEX + 36, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!mergeItemStack(originalStack, PLAYER_INV_INDEX, PLAYER_INV_INDEX + 27, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (originalStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (originalStack.getCount() == copiedStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, originalStack);
		}

		return copiedStack;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	public ItemStack getBelt() {
		return belt;
	}

	public static ContainerMobCharmBelt fromBuffer(int windowId, PlayerInventory playerInventory, PacketBuffer packetBuffer) {
		Hand hand = packetBuffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		return new ContainerMobCharmBelt(windowId, playerInventory, playerInventory.player.getHeldItem(hand));
	}
}
