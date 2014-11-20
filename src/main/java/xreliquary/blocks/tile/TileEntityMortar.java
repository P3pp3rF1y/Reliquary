package xreliquary.blocks.tile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import lib.enderwizards.sandstone.blocks.tile.TileEntityInventory;
import lib.enderwizards.sandstone.init.ContentHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;
import xreliquary.util.potions.PotionEssence;
import xreliquary.util.potions.PotionIngredient;
import xreliquary.util.potions.PotionMap;
import xreliquary.util.potions.XRPotionHelper;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMortar extends TileEntityInventory {

    // counts the number of times the player has right clicked the block
    // arbitrarily setting the number of times the player needs to grind the
    // materials to five.
    private int pestleUsedCounter;
    private String customInventoryName;

    public TileEntityMortar() {
        //inventory size
        super(3);
        pestleUsedCounter = 0;
    }

    @Override
    public Packet getDescriptionPacket() {
        return super.getDescriptionPacket();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
    }

    @Override
    public void updateEntity() {
        // do stuff on tick? I don't think we need this to tick.
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList items = tag.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte b0 = item.getByte("Slot");

            if (b0 >= 0 && b0 < this.inventory.length) {
                this.inventory[b0] = ItemStack.loadItemStackFromNBT(item);
            }
        }

        this.pestleUsedCounter = tag.getShort("pestleUsed");

        if (tag.hasKey("CustomName", 8)) {
            this.customInventoryName = tag.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setShort("pestleUsed", (short) this.pestleUsedCounter);
        NBTTagList items = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound item = new NBTTagCompound();
                this.inventory[i].writeToNBT(item);
                item.setByte("Slot", (byte) i);
                items.appendTag(item);
            }
        }

        tag.setTag("Items", items);

        if (this.hasCustomInventoryName()) {
            tag.setString("CustomName", this.getInventoryName());
        }
    }

    // gets the contents of the tile entity as an array of inventory
    public ItemStack[] getItemStacks() {
        return inventory;
    }

    // increases the "pestleUsed" counter, checks to see if it is at its limit
    public void usePestle() {
        int itemCount = 0;
        List<PotionIngredient> potionIngredients = new ArrayList<PotionIngredient>();
        for (ItemStack item : this.getItemStacks()) {
            if (item == null)
                continue;
            ++itemCount;
            potionIngredients.add(PotionMap.getIngredient(item));
        }
        if (itemCount > 1) {
            pestleUsedCounter++;
            spawnPestleParticles();
        }
        if (pestleUsedCounter >= Reference.PESTLE_USAGE_MAX) {
            //we've "maxed" the pestle counter and we need to see if the essence would contain potion effects.
            //if it doesn't, just return the ingredients to the player, we are nice like that.
            PotionEssence resultEssence = new PotionEssence(potionIngredients.toArray(new PotionIngredient[potionIngredients.size()]));
            if (resultEssence.getEffects().size() == 0) {
                pestleUsedCounter = 0;
                for (int clearSlot = 0; clearSlot < this.getSizeInventory(); ++clearSlot) {
                    if (!this.worldObj.isRemote) {
                        EntityItem itemEntity = new EntityItem(worldObj, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, this.getStackInSlot(clearSlot));
                        worldObj.spawnEntityInWorld(itemEntity);
                    }
                    this.setInventorySlotContents(clearSlot, null);
                }
            } else {
                for (int clearSlot = 0; clearSlot < this.getSizeInventory(); ++clearSlot) {
                    this.setInventorySlotContents(clearSlot, null);
                }
                pestleUsedCounter = 0;
                if (worldObj.isRemote)
                    return;
                ItemStack resultItem = new ItemStack(ContentHandler.getItem(Names.potion_essence), 1, 0);
                resultItem.setTagCompound(resultEssence.writeToNBT());


                EntityItem itemEntity = new EntityItem(worldObj, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, resultItem);
                worldObj.spawnEntityInWorld(itemEntity);
            }
        }
    }

    public void spawnPestleParticles() {
        worldObj.spawnParticle("smoke", xCoord + 0.5D, yCoord + 0.15D, zCoord + 0.5D, 0.0D, 0.1D, 0.0D);
    }

    @Override
    public String getInventoryName() {
        return customInventoryName;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customInventoryName != null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && var1.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack ist) {
        // don't allow essence/items in slots after the third one.
        //only allow valid potion items

        //also now doesn't allow the same item twice.
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) == null)
                continue;
            if (this.getStackInSlot(i).isItemEqual(ist))
                return false;
        }
        return slot <= 3 && (XRPotionHelper.isItemIngredient(ist) || XRPotionHelper.isItemEssence(ist));
    }
}
