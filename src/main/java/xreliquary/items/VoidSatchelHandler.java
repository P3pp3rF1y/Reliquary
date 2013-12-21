package xreliquary.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.ICraftingHandler;

public class VoidSatchelHandler implements ICraftingHandler {

	@Override
	public void onCrafting(EntityPlayer player, ItemStack satchel, IInventory craftMatrix) {
		if (satchel == null)
			return;
		if (satchel.getItem() != XRItems.voidSatchel)
			return;
		// handles creation of VoidSatchel
		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			if (craftMatrix.getStackInSlot(slot) == null) {
				continue;
			}
			if (craftMatrix.getStackInSlot(slot).getItem() != XRItems.voidTear) {
				continue;
			}
			ItemStack tear = craftMatrix.getStackInSlot(slot);
			NBTTagCompound tearData = tear.getTagCompound();
			if (tearData == null) {
				continue;
			}
			int type = tearData.getShort("itemID");
			int meta = tearData.getShort("itemMeta");
			int quantity = tearData.getShort("itemQuantity");
			int leftover = 0;
			int capacity = Reference.CAPACITY_UPGRADE_INCREMENT;
			if (quantity > capacity) {
				leftover = quantity - capacity;
				quantity = capacity;
			}
			NBTTagCompound satchelData = new NBTTagCompound();
			satchelData.setShort("itemID", (short) type);
			satchelData.setShort("itemMeta", (short) meta);
			satchelData.setShort("itemQuantity", (short) quantity);
			satchelData.setShort("capacity", (short) capacity);
			satchel.setTagCompound(satchelData);
			if (leftover > 0) {
				player.worldObj.playSoundAtEntity(player, "random.glass", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
				while (leftover > 0) {
					ItemStack spillage = new ItemStack(type, 1, meta);
					if (leftover > spillage.getMaxStackSize()) {
						spillage.stackSize = spillage.getMaxStackSize();
						leftover -= spillage.getMaxStackSize();
					} else {
						spillage.stackSize = leftover;
						leftover = 0;
					}
					if (player.worldObj.isRemote) {
						continue;
					}
					EntityItem item = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, spillage);
					player.worldObj.spawnEntityInWorld(item);
				}
			}
		}
		// handles upgrades for VoidSatchel
		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			if (craftMatrix.getStackInSlot(slot) == null) {
				continue;
			}
			if (craftMatrix.getStackInSlot(slot).getItem() != XRItems.voidSatchel) {
				continue;
			}
			ItemStack oldSatchel = craftMatrix.getStackInSlot(slot);
			NBTTagCompound oldSatchelData = oldSatchel.getTagCompound();
			if (oldSatchelData == null) {
				continue;
			}
			int type = oldSatchelData.getShort("itemID");
			int meta = oldSatchelData.getShort("itemMeta");
			int quantity = oldSatchelData.getShort("itemQuantity");
			int capacity = oldSatchelData.getShort("capacity");
			if (capacity >= 32000) {
				for (int slot0 = 0; slot0 < craftMatrix.getSizeInventory(); slot++) {
					if (craftMatrix.getStackInSlot(slot0) == null) {
						continue;
					}
					if (craftMatrix.getStackInSlot(slot0).getItem() == XRItems.emptyVoidTear) {
						craftMatrix.getStackInSlot(slot0).stackSize++;
					}
				}
			} else {
				capacity += 3 * Reference.CAPACITY_UPGRADE_INCREMENT;
			}
			NBTTagCompound satchelData = new NBTTagCompound();
			satchelData.setShort("itemID", (short) type);
			satchelData.setShort("itemMeta", (short) meta);
			satchelData.setShort("itemQuantity", (short) quantity);
			satchelData.setShort("capacity", (short) capacity);
			satchel.setTagCompound(satchelData);
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {

	}

}
