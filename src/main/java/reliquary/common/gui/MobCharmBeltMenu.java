package reliquary.common.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.items.MobCharmRegistry;

import static reliquary.init.ModItems.MOB_CHAR_BELT_MENU_TYPE;

public class MobCharmBeltMenu extends AbstractContainerMenu {
	public MobCharmBeltMenu(int windowId, Inventory playerInventory, ItemStack belt) {
		super(MOB_CHAR_BELT_MENU_TYPE.get(), windowId);
		this.belt = belt;

		for (int i = 0; i < getFirstPlayerInventoryIndex(); i++) {
			addSlot(new SlotMobCharm(belt, i));
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

	private final ItemStack belt;

	private static int getFirstPlayerInventoryIndex() {
		return MobCharmRegistry.getRegisteredNames().size() + 1;
	}

	@Override

	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		Slot slot = null;
		if (slotId >= 0 && slotId < slots.size()) {
			slot = slots.get(slotId);
		}
		ItemStack slotStack = slot == null ? ItemStack.EMPTY : slot.getItem();

		//prevent moving belt out of its slot
		if (slot != null && !slotStack.isEmpty() && slotStack.getItem() == ModItems.MOB_CHARM_BELT.get() && slotStack == player.getMainHandItem()) {
			return;
		}

		//overriden here so that on shift click it doesn't retry and thus move more charms out of belt
		if (slotId >= 0 && slotId < getFirstPlayerInventoryIndex() && clickTypeIn == ClickType.QUICK_MOVE && (dragType == 0 || dragType == 1)) {
			if (slot != null && slot.mayPickup(player)) {
				quickMoveStack(player, slotId);
			}
			return;
		}

		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack copiedStack = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack originalStack = slot.getItem();
			copiedStack = originalStack.copy();

			int playerInvIndex = getFirstPlayerInventoryIndex();

			if (index < playerInvIndex) {
				if (!moveItemStackTo(originalStack, playerInvIndex, playerInvIndex + 36, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(originalStack, copiedStack);
			} else if (index < playerInvIndex + 36) {
				if (originalStack.getItem() == ModItems.MOB_CHARM.get()) {
					if (!moveItemStackTo(originalStack, 0, playerInvIndex, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < playerInvIndex + 27) {
					if (!moveItemStackTo(originalStack, playerInvIndex + 27, playerInvIndex + 36, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!moveItemStackTo(originalStack, playerInvIndex, playerInvIndex + 27, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (originalStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (originalStack.getCount() == copiedStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, originalStack);
		}

		return copiedStack;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}

	public ItemStack getBelt() {
		return belt;
	}

	public static MobCharmBeltMenu fromBuffer(int windowId, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
		InteractionHand hand = packetBuffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		return new MobCharmBeltMenu(windowId, playerInventory, playerInventory.player.getItemInHand(hand));
	}
}
