package xreliquary.items;

import lib.enderwizards.sandstone.init.ContentInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.lib.Names;

@ContentInit
public class ItemVoidTear extends ItemTear {

    public ItemVoidTear() {
        super(Names.void_tear);
        this.useAmount = true;
        this.absorbByDefault = false;
    }

    @Override
    protected void onAbsorb(ItemStack stack, EntityPlayer player) {
        stack.getTagCompound().setShort("itemQuantity", ((Integer) (stack.getTagCompound().getShort("itemQuantity") + 1)).shortValue());
    }

    @Override
    protected boolean shouldEmpty(ItemStack stack, EntityPlayer player, IInventory inventory, int limit) {
        NBTTagCompound tag = stack.getTagCompound();
        ItemStack contents = this.getStackFromTear(stack);

        int quantity = tag.getShort("itemQuantity");
        int minQuantity = quantity - contents.getMaxStackSize();
        while (quantity > Math.max(0, minQuantity)) {
            if (!tryToAddToInventory(contents, inventory, limit)) {
                break;
            }
            quantity--;
        }

        if (quantity == 0) {
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
            return true;
        } else {
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
            tag.setShort("itemQuantity", (short) quantity);
            stack.setTagCompound(tag);
            return false;
        }
    }

    public boolean tryToAddToInventory(ItemStack contents, IInventory inventory, int limit) {
        for (int slot = 0; slot < Math.min(inventory.getSizeInventory(), (limit > 0 ? limit : inventory.getSizeInventory())); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            if (inventory.getStackInSlot(slot).isItemEqual(contents)) {
                if (inventory.getStackInSlot(slot).stackSize == inventory.getStackInSlot(slot).getMaxStackSize()) {
                    continue;
                }
                inventory.getStackInSlot(slot).stackSize++;
                return true;
            }
        }
        for (int slot = 0; slot < Math.min(inventory.getSizeInventory(), (limit > 0 ? limit : inventory.getSizeInventory())); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot, new ItemStack(contents.getItem(), contents.stackSize, contents.getItemDamage()));
                return true;
            }
        }
        return false;
    }
}
