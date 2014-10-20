package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.ContentHelper;
import lib.enderwizards.sandstone.util.InventoryHelper;
import lib.enderwizards.sandstone.util.LanguageHelper;
import lib.enderwizards.sandstone.util.NBTHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.Reliquary;
import xreliquary.init.XRRecipes;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

@ContentInit
public class ItemVoidTearEmpty extends ItemBase {
    public ItemVoidTearEmpty() {
        super(Names.void_tear_empty);
        this.setCreativeTab(Reliquary.CREATIVE_TAB);
        this.setMaxStackSize(16);
        canRepair = false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (!world.isRemote) {
            ItemStack createdTear = buildTear(ist, player, player.inventory);
            if (createdTear != null) {
                --ist.stackSize;
                player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
                if (ist.stackSize == 0)
                    return createdTear;
                else
                    addItemToInventory(player, createdTear);
            }
        }
        return ist;
    }

    @Override
    public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {

            if (world.getTileEntity(x, y, z) instanceof IInventory) {
                IInventory inventory = (IInventory) world.getTileEntity(x, y, z);

                ItemStack createdTear = buildTear(ist, player, inventory);
                if (createdTear != null) {
                    --ist.stackSize;
                    player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
                    if (ist.stackSize == 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, createdTear);
                    else
                        addItemToInventory(player, createdTear);
                    return true;
                }
            }
        }
        return false;
    }

    protected void addItemToInventory(EntityPlayer player, ItemStack ist) {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            if (player.inventory.getStackInSlot(i) == null) {
                player.inventory.setInventorySlotContents(i, ist);
                return;
            }
        }
        player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, ist));
    }

    protected ItemStack buildTear(ItemStack ist, EntityPlayer player, IInventory inventory) {
        ItemStack target = InventoryHelper.getTargetItem(ist, inventory, false);
        if(target == null)
            return null;
        ItemStack filledTear = new ItemStack(ContentHandler.getItem(Names.void_tear), 1, 0);

        NBTHelper.setString("itemID", filledTear, ContentHelper.getIdent(target.getItem()));
        NBTHelper.setShort("itemMeta", filledTear, (short) target.getItemDamage());

        int quantity = InventoryHelper.getItemQuantity(target, inventory);
        InventoryHelper.removeItem(target, inventory, quantity);
        NBTHelper.setInteger("itemQuantity", filledTear, quantity);
        //comes in with auto-drain enabled, I think this is what people would prefer.
        NBTHelper.setBoolean("enabled", filledTear, true);

        return filledTear;
    }
}
