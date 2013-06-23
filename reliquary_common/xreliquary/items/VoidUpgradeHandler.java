package xreliquary.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.blocks.XRBlocks;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.ICraftingHandler;

public class VoidUpgradeHandler implements ICraftingHandler {
	public static int getCapacity(NBTTagCompound nbt) {
		return nbt.getShort("capacity");
	}

	public static boolean canUpgrade(NBTTagCompound nbt) {
		return !(getCapacity(nbt) >= 32000);
	}

	// handles the upgrading of void items capacities. may be extended later to
	// work with other upgradable items.
	public static void upgradeCapacity(NBTTagCompound nbt, int upgrades) {
		for (; upgrades > 0; upgrades--) {
			if (!canUpgrade(nbt)) return;
			setCapacity(nbt, getCapacity(nbt) + Reference.CAPACITY_UPGRADE_INCREMENT);
		}
	}

	// increases the capacity. safe to cast at this point because canUpgrade
	// performs the
	// casting validation for us.
	private static void setCapacity(NBTTagCompound nbt, int i) {
		nbt.setShort("capacity", (short)i);
	}

	@Override
	public void onCrafting(EntityPlayer player, ItemStack voidItem, IInventory craftMatrix) {
		if (voidItem == null) return;
		if (!isVoidUpgradable(voidItem.getItem())) return;
		// handles upgrades for any of the void items.
		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			if (craftMatrix.getStackInSlot(slot) == null) {
				continue;
			}
			// this portion of the for loop is looking for the void item being
			// upgraded
			// it just keeps skipping until it finds it.
			ItemStack originalVoidItem = craftMatrix.getStackInSlot(slot);
			NBTTagCompound originalCapacityData = originalVoidItem.getTagCompound();
			if (originalCapacityData == null) {
				continue;
			}
			// this portion basically prevents the void tear/wraith node used to
			// upgrade
			// from being "eaten", because you've already reached your capacity
			// with that item.
			// if it can handle the upgrade, it moves forward with the capacity
			// increase.
			for (int slot0 = 0; slot0 < craftMatrix.getSizeInventory(); slot0++) {
				if (craftMatrix.getStackInSlot(slot0) == null) {
					continue;
				}
				if (craftMatrix.getStackInSlot(slot0).getItem() == XRItems.emptyVoidTear) {
					if (getCapacity(originalCapacityData) + Reference.CAPACITY_UPGRADE_INCREMENT >= 32000) {
						craftMatrix.getStackInSlot(slot0).stackSize++;
					} else {
						upgradeCapacity(originalCapacityData, 1);
					}
				} else if (craftMatrix.getStackInSlot(slot0).getItem() == Item.itemsList[XRBlocks.wraithNode.blockID]) {
					if (getCapacity(originalCapacityData) + Reference.CAPACITY_UPGRADE_INCREMENT * 4 >= 32000) {
						craftMatrix.getStackInSlot(slot0).stackSize++;
					} else {
						upgradeCapacity(originalCapacityData, 4);
					}
				}
			}
			int capacity = originalCapacityData.getShort("capacity");
			NBTTagCompound newVoidItemData = new NBTTagCompound();
			newVoidItemData.setShort("capacity", (short)capacity);
			if (voidItem.getItem() instanceof ItemVoidSatchel) {
				// only void satchels need to have their item ID/meta/quantity
				// set.
				// the others are fine just having their capacity upgraded.
				int type = originalCapacityData.getShort("itemID");
				int meta = originalCapacityData.getShort("itemMeta");
				int quantity = originalCapacityData.getShort("itemQuantity");
				newVoidItemData.setShort("itemID", (short)type);
				newVoidItemData.setShort("itemMeta", (short)meta);
				newVoidItemData.setShort("itemQuantity", (short)quantity);
			}
			voidItem.setTagCompound(newVoidItemData);
		}
	}

	private boolean isVoidUpgradable(Item item) {
		return item instanceof ItemWithCapacity;
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {
	}
}
