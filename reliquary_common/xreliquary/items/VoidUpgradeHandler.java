package xreliquary.items;

import cpw.mods.fml.common.ICraftingHandler;
import xreliquary.blocks.XRBlocks;
import xreliquary.lib.Reference;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class VoidUpgradeHandler implements ICraftingHandler  {

    public static int getCapacity(ItemStack ist) {
        if (!(ist.getItem() instanceof ItemXR))
            return 0;
        return ((ItemXR)ist.getItem()).getShort("capacity", ist);
    }

    public static boolean canUpgrade(ItemStack ist) {
        return (ist.getItem() instanceof ItemXR) && !(getCapacity(ist) >= 32000); 
    }

    public static Item[] validItems() {
        return new Item[] { 
                XRItems.iceRod, 
                XRItems.midasTouchstone, 
                XRItems.voidSatchel, 
                XRItems.sojournerStaff 
        };
    }

    //returns the number of wraith nodes or void tears (returns void tear values)
    //which could not be used to upgrade the itemstack (due to maximum capacity)
    //kind of silly, I chose a different implementation in the end, but it could've gone either way.
   
    public static int upgradeCapacity(ItemStack ist, int upgrades) {
        for (; upgrades > 0; upgrades--) {
            if (!canUpgrade(ist))
                return upgrades;
            setCapacity(ist, getCapacity(ist) + Reference.CAPACITY_UPGRADE_INCREMENT);
        }
        return upgrades;
    }

    //increases the capacity. safe to cast at this point because canUpgrade performs the
    //casting validation for us.
    private static void setCapacity(ItemStack ist, int i) {
        ((ItemXR)ist.getItem()).setShort("capacity", ist, i);
    }

    @Override
    public void onCrafting(EntityPlayer player, ItemStack voidItem,
            IInventory craftMatrix) {
        if (voidItem == null)
            return;
        if (!isVoidUpgradable(voidItem.getItem()))
            return;

        //handles the creation of void satchels, specifically.
        if (voidItem.getItem() instanceof ItemVoidSatchel) {   
            for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {

                //if the loop finds no tears, this isn't the void satchel recipe, so it bails.
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
                    player.worldObj.playSoundAtEntity(player,"random.glass",0.1F,
                            0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
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

        // handles upgrades for any of the void items.
        for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
            if (craftMatrix.getStackInSlot(slot) == null)
                continue;        
            
            ItemStack originalVoidItem = craftMatrix.getStackInSlot(slot);
            NBTTagCompound originalCapacityData = originalVoidItem.getTagCompound();
            if (originalCapacityData == null) {
                continue;
            }

            NBTTagCompound newVoidItemData = new NBTTagCompound();
            
            if (voidItem.getItem() instanceof ItemVoidSatchel) {   
                //only void satchels need to have their item ID/meta/quantity set.
                int type = originalCapacityData.getShort("itemID");
                int meta = originalCapacityData.getShort("itemMeta");
                int quantity = originalCapacityData.getShort("itemQuantity");                

                newVoidItemData.setShort("itemID", (short) type);
                newVoidItemData.setShort("itemMeta", (short) meta);
                newVoidItemData.setShort("itemQuantity", (short) quantity);
            }
            
            int capacity = originalCapacityData.getShort("capacity");
            
            //this portion basically prevents the void tear/wraith node used to upgrade
            //from being "eaten", because you've already reached your capacity with that item.            
            for (int slot0 = 0; slot0 < craftMatrix.getSizeInventory(); slot++) {
                if (craftMatrix.getStackInSlot(slot0) == null) {
                    continue;
                }
                if (craftMatrix.getStackInSlot(slot0).getItem() == XRItems.emptyVoidTear) {
                    if (getCapacity(voidItem) + Reference.CAPACITY_UPGRADE_INCREMENT >= 32000)
                        craftMatrix.getStackInSlot(slot0).stackSize++;
                    else
                        upgradeCapacity(voidItem, 1);
                } else if (craftMatrix.getStackInSlot(slot0).getItem() == Item.itemsList[XRBlocks.wraithNode.blockID]) {
                    if (getCapacity(voidItem) + (Reference.CAPACITY_UPGRADE_INCREMENT * 4) >= 32000)
                        craftMatrix.getStackInSlot(slot0).stackSize++;
                    else
                        upgradeCapacity(voidItem, 4);
                }
            }
            
            newVoidItemData.setShort("capacity", (short) capacity);
            voidItem.setTagCompound(newVoidItemData);
        }
    }

    private boolean isVoidUpgradable(Item item) {
        for (Item validItem : validItems()) {
            if (item == validItem)
                return true;
        }
        return false;
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item) {

    }
}
