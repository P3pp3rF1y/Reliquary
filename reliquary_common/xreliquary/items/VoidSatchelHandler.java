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
    public void onCrafting(EntityPlayer player, ItemStack voidItem,
            IInventory craftMatrix) {
        if (voidItem == null)
            return;
        if (voidItem.getItem() != XRItems.voidSatchel)
            return;
        // handles the creation of void satchels, specifically.
        if (voidItem.getItem() instanceof ItemVoidSatchel) {
            for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {

                // if the loop finds no tears, this isn't the void satchel
                // recipe, so it bails.
                ItemStack tear = craftMatrix.getStackInSlot(slot);
                if (tear == null) {
                    continue;
                }
                if (!(tear.getItem() instanceof ItemVoidTear)) {
                    continue;
                }

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
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {

    }

}
