package xreliquary.items;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.lib.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVoidTear extends ItemXR {

    public ItemVoidTear(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(Reliquary.tabsXR);
        this.setUnlocalizedName(Names.VOID_TEAR_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    public void addInformation(ItemStack ist, EntityPlayer par2EntityPlayer,
            List par3List, boolean par4) {
        NBTTagCompound tag = ist.getTagCompound();
        String details0 = "This Void Tear currently ";
        String details1 = "holds ";
        if (tag == null
                || new ItemStack(tag.getShort("itemID"), 1,
                        tag.getShort("itemMeta")).getItem() == null) {
            details1 += "nothing.";
        } else {
            ItemStack contents = new ItemStack(tag.getShort("itemID"), 1,
                    tag.getShort("itemMeta"));
            String itemName = contents.getDisplayName();
            details1 += tag.getShort("itemQuantity") + "x " + itemName;
        }
        par3List.add(details0);
        par3List.add(details1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world,
            EntityPlayer player) {
        unloadContentsIntoPlayerInventory(ist, player.inventory, player);
        return ist;
    }

    @Override
    public boolean onItemUseFirst(ItemStack ist, EntityPlayer player,
            World world, int x, int y, int z, int side, float hitX, float hitY,
            float hitZ) {
        if (world.getBlockTileEntity(x, y, z) instanceof IInventory) {
            IInventory inventory = (IInventory) world.getBlockTileEntity(x, y,
                    z);
            unloadContentsIntoInventory(ist, inventory, player);
            return false;
        }
        return false;
    }

    public void unloadContentsIntoInventory(ItemStack ist,
            IInventory inventory, EntityPlayer player) {
        NBTTagCompound tearTag = ist.getTagCompound();
        if (tearTag == null)
            return;
        ItemStack contents = new ItemStack(tearTag.getShort("itemID"), 1,
                tearTag.getShort("itemMeta"));
        int quantity = tearTag.getShort("itemQuantity");
        while (quantity > 0) {
            if (!tryToAddToInventory(contents, inventory)) {
                break;
            }
            quantity--;
        }
        if (quantity == 0) {
            addEmptyTearToPlayerInventory(player);
            player.worldObj
                    .playSoundAtEntity(
                            player,
                            "random.orb",
                            0.1F,
                            0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand
                                    .nextFloat()) * 0.7F + 1.8F));

            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            return;
        } else {
            player.worldObj
                    .playSoundAtEntity(
                            player,
                            "random.orb",
                            0.1F,
                            0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand
                                    .nextFloat()) * 0.7F + 1.8F));
            tearTag.setShort("itemQuantity", (short) quantity);
            ist.setTagCompound(tearTag);
        }
        return;
    }

    public void unloadContentsIntoPlayerInventory(ItemStack ist,
            IInventory inventory, EntityPlayer player) {
        NBTTagCompound tearTag = ist.getTagCompound();
        if (tearTag == null)
            return;
        ItemStack contents = new ItemStack(tearTag.getShort("itemID"), 1,
                tearTag.getShort("itemMeta"));
        int quantity = tearTag.getShort("itemQuantity");
        while (quantity > 0) {
            if (!tryToAddToPlayerInventory(contents, inventory, player)) {
                break;
            }
            quantity--;
        }
        if (quantity == 0) {
            addEmptyTearToPlayerInventory(player);
            player.worldObj
                    .playSoundAtEntity(
                            player,
                            "random.orb",
                            0.1F,
                            0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand
                                    .nextFloat()) * 0.7F + 1.8F));
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
            return;
        } else {
            player.worldObj
                    .playSoundAtEntity(
                            player,
                            "random.orb",
                            0.1F,
                            0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand
                                    .nextFloat()) * 0.7F + 1.8F));
            tearTag.setShort("itemQuantity", (short) quantity);
            ist.setTagCompound(tearTag);
        }
        return;
    }

    private void addEmptyTearToPlayerInventory(EntityPlayer player) {
        if (!player.inventory.addItemStackToInventory(new ItemStack(
                XRItems.emptyVoidTear, 1))) {
            player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj,
                    player.posX, player.posY, player.posZ, new ItemStack(
                            XRItems.emptyVoidTear, 1)));
        }
    }

    public boolean tryToAddToInventory(ItemStack contents, IInventory inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            if (inventory.getStackInSlot(slot).isItemEqual(contents)) {
                if (inventory.getStackInSlot(slot).stackSize == inventory
                        .getStackInSlot(slot).getMaxStackSize()) {
                    continue;
                }
                inventory.getStackInSlot(slot).stackSize++;
                return true;
            }
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot,
                        new ItemStack(contents.itemID, contents.stackSize,
                                contents.getItemDamage()));
                return true;
            }
        }
        return false;
    }

    public boolean tryToAddToPlayerInventory(ItemStack contents,
            IInventory inventory, EntityPlayer player) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            if (slot >= player.inventory.mainInventory.length) {
                continue;
            }
            if (inventory.getStackInSlot(slot).isItemEqual(contents)) {
                if (inventory.getStackInSlot(slot).stackSize == inventory
                        .getStackInSlot(slot).getMaxStackSize()) {
                    continue;
                }
                inventory.getStackInSlot(slot).stackSize++;
                return true;
            }
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot,
                        new ItemStack(contents.itemID, contents.stackSize,
                                contents.getItemDamage()));
                return true;
            }
        }
        return false;
    }
}
