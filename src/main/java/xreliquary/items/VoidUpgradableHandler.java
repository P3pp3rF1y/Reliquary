package xreliquary.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.blocks.XRBlocks;
import xreliquary.lib.Reference;
import cpw.mods.fml.common.ICraftingHandler;

public class VoidUpgradableHandler implements ICraftingHandler {

    @Override
    public void onCrafting(EntityPlayer player, ItemStack voidItem,
            IInventory craftMatrix) {
        if (voidItem == null)
            return;
        if (!(voidItem.getItem() instanceof IVoidUpgradable))
            return;
        for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
            if (craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            if (craftMatrix.getStackInSlot(slot).getItem() != XRItems.voidTear && craftMatrix.getStackInSlot(slot).getItem() != Item.itemsList[XRBlocks.wraithNode.blockID]) {
                continue;
            }
            if (voidItem.getItem() instanceof ItemVoidSatchel) {
                
                ItemStack tear = craftMatrix.getStackInSlot(slot);
                
                if (!(tear.getItem() instanceof ItemVoidTear)) continue;
                
                NBTTagCompound tearData = tear.getTagCompound();
                
                if (tearData == null) continue;
                
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
                voidItem.setTagCompound(satchelData);
                if (leftover > 0) {
                    player.worldObj
                    .playSoundAtEntity(
                            player,
                            "random.glass",
                            0.1F,
                            0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand
                                    .nextFloat()) * 0.7F + 1.8F));
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
                        EntityItem item = new EntityItem(player.worldObj,
                                player.posX, player.posY, player.posZ, spillage);
                        player.worldObj.spawnEntityInWorld(item);
                    }
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
            voidItem.setTagCompound(satchelData);
        }
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {

    }

}
