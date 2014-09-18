package xreliquary.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.init.ContentHandler;
import lib.enderwizards.sandstone.init.ContentInit;
import lib.enderwizards.sandstone.items.ItemBase;
import lib.enderwizards.sandstone.items.ItemToggleable;
import lib.enderwizards.sandstone.util.InventoryHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import xreliquary.lib.Names;
import xreliquary.lib.Reference;

import java.util.List;

@ContentInit
public class ItemVoidTear extends ItemToggleable {

    public ItemVoidTear() {
        super(Names.void_tear);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        canRepair = false;
    }

    @SideOnly(Side.CLIENT)
    private IIcon iconOverlay;

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.epic;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        iconOverlay = iconRegister.registerIcon(Reference.MOD_ID.toLowerCase() + ":" + Names.void_tear_overlay);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        NBTTagCompound tag = stack.getTagCompound();
        String holds;
        if (tag == null || new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta")).getItem() == null) {
            holds = "nothing";
        } else {
            ItemStack contents = new ItemStack((Item) Item.itemRegistry.getObject(tag.getString("itemID")), 1, tag.getShort("itemMeta"));
            String itemName = contents.getDisplayName();
            holds = "" + EnumChatFormatting.YELLOW + tag.getShort("itemQuantity") + " of " + itemName;
        }
        this.formatTooltip(ImmutableMap.of("holds", holds), stack, list);
        if (this.isEnabled(stack))
            list.add(EnumChatFormatting.LIGHT_PURPLE + "Automagically absorbs items.");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        if (renderPass == 1 && stack.getTagCompound() != null && this.isEnabled(stack))
            return iconOverlay;
        else
            return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean f) {
        if (world.isRemote)
            return;
        EntityPlayer player;
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        } else
            return;
        if (stack.getTagCompound() != null && this.isEnabled(stack)) {
            Item item = ContentHandler.getItem(stack.getTagCompound().getString("itemID"));
            ItemStack newStack = new ItemStack(item, 0, (int) stack.getTagCompound().getShort("itemMeta"));
            if (InventoryHelper.consumeItem(newStack, player, newStack.getMaxStackSize())) {
                stack.getTagCompound().setShort("itemQuantity", ((Integer) (stack.getTagCompound().getShort("itemQuantity") + 1)).shortValue());
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack newStack = super.onItemRightClick(stack, world, player);
        if(player.isSneaking())
            return newStack;

        unloadContentsIntoInventory(stack, player.inventory, player, true);
        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }

        if (world.getTileEntity(x, y, z) instanceof IInventory) {
            IInventory inventory = (IInventory) world.getTileEntity(x, y, z);
            unloadContentsIntoInventory(stack, inventory, player, false);
            return true;
        }
        return false;
    }

    public void unloadContentsIntoInventory(ItemStack stack, IInventory inventory, EntityPlayer player, boolean playerInv) {
        NBTTagCompound tearTag = stack.getTagCompound();
        if (tearTag == null)
            return;
        ItemStack contents = new ItemStack(ContentHandler.getItem(tearTag.getString("itemID")), 1, tearTag.getShort("itemMeta"));
        int quantity = tearTag.getShort("itemQuantity");
        int minQuantity = quantity - contents.getMaxStackSize();
        while (quantity > Math.max(0, minQuantity)) {
            if (playerInv) {
                if (!tryToAddToPlayerInventory(contents, inventory, player)) {
                    break;
                }
            } else {
                if (!tryToAddToInventory(contents, inventory)) {
                    break;
                }
            }
            quantity--;
        }
        if (quantity == 0) {
            addEmptyTearToPlayerInventory(player);
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
            return;
        } else {
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
            tearTag.setShort("itemQuantity", (short) quantity);
            stack.setTagCompound(tearTag);
        }
        return;
    }

    private void addEmptyTearToPlayerInventory(EntityPlayer player) {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(ContentHandler.getItem(Names.void_tear_empty)));
    }

    public boolean tryToAddToInventory(ItemStack contents, IInventory inventory) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
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
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot, new ItemStack(contents.getItem(), contents.stackSize, contents.getItemDamage()));
                return true;
            }
        }
        return false;
    }

    public boolean tryToAddToPlayerInventory(ItemStack contents, IInventory inventory, EntityPlayer player) {
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (slot >= player.inventory.mainInventory.length) {
                continue;
            }
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
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (slot >= player.inventory.mainInventory.length) {
                continue;
            }
            if (inventory.getStackInSlot(slot) == null) {
                inventory.setInventorySlotContents(slot, new ItemStack(contents.getItem(), contents.stackSize, contents.getItemDamage()));
                return true;
            }
        }
        return false;
    }
}
