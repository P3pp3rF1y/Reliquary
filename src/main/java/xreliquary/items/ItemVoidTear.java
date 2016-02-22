package xreliquary.items;


import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xreliquary.init.ModItems;
import xreliquary.reference.Names;
import xreliquary.reference.Settings;
import xreliquary.util.InventoryHelper;
import xreliquary.util.LanguageHelper;
import xreliquary.util.NBTHelper;
import xreliquary.util.StackHelper;

import java.util.List;

public class ItemVoidTear extends ItemToggleable {

    public ItemVoidTear() {
        super(Names.void_tear);
        setMaxStackSize(1);
        canRepair = false;
        this.setCreativeTab(null);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {

        this.formatTooltip(null, stack, list);

        ItemStack contents = this.getContainedItem(stack);

        if (contents == null)
            return;

        if(this.isEnabled(stack)) {
            LanguageHelper.formatTooltip("tooltip.absorb_active", ImmutableMap.of("item", EnumChatFormatting.YELLOW + contents.getDisplayName()), stack, list);
            list.add(LanguageHelper.getLocalization("tooltip.absorb_tear"));
        }
        LanguageHelper.formatTooltip("tooltip.tear_quantity", ImmutableMap.of("item", contents.getDisplayName(), "amount", Integer.toString(contents.stackSize)), stack, list);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack ist, World world, EntityPlayer player) {
        if (!world.isRemote) {

            if (NBTHelper.getInteger("itemQuantity", ist) == 0)
                return new ItemStack(ModItems.emptyVoidTear, 1, 0);

            MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(world, player, false);

            //not enabling void tear if player tried to deposit everything into inventory but there wasn't enough space
            if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                    && world.getTileEntity(movingObjectPosition.getBlockPos()) instanceof IInventory
                    && player.isSneaking())
                return ist;

            if (player.isSneaking())
                return super.onItemRightClick(ist, world, player);
            if (this.attemptToEmptyIntoInventory(ist, player, player.inventory, player.inventory.mainInventory.length)) {
                player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
                NBTHelper.resetTag(ist);
                return new ItemStack(ModItems.emptyVoidTear, 1, 0);
            }
        }

        player.inventoryContainer.detectAndSendChanges();
        return ist;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean f) {
        if (!world.isRemote) {
            if (!this.isEnabled(stack) || !(entity instanceof EntityPlayer))
                return;

            EntityPlayer player = (EntityPlayer) entity;

            ItemStack contents = this.getContainedItem(stack);

            int itemQuantity = InventoryHelper.getItemQuantity(contents, player.inventory);

            if (contents.stackSize < Settings.VoidTear.itemLimit && itemQuantity > contents.getMaxStackSize() && InventoryHelper.consumeItem(contents, player, contents.getMaxStackSize(), itemQuantity - contents.getMaxStackSize())) {
                //doesn't absorb in creative mode.. this is mostly for testing, it prevents the item from having unlimited *whatever* for eternity.
                if (!player.capabilities.isCreativeMode)
                    NBTHelper.setInteger("itemQuantity", stack, NBTHelper.getInteger("itemQuantity", stack) + itemQuantity - contents.getMaxStackSize());
            }

            attemptToReplenishSingleStack(player, stack);
        }
    }

    public void attemptToReplenishSingleStack(EntityPlayer player, ItemStack ist) {
        int preferredSlot = -1;
        int stackCount = 0;
        IInventory inventory = player.inventory;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stackFound = inventory.getStackInSlot(slot);
            if (stackFound == null) {
                continue;
            }
            if (StackHelper.isItemAndNbtEqual(stackFound, getContainedItem(ist))) {
                if (preferredSlot == -1) preferredSlot = slot;
                stackCount += 1;
            }
        }

        //use first empty slot for new stack if there's no stack to restock
        if(preferredSlot == -1) {
            preferredSlot = player.inventory.getFirstEmptyStack();
            if (preferredSlot > -1)
                stackCount = 1;
        }

        if (stackCount == 1 && preferredSlot != -1) {
            ItemStack stackToIncrease = player.inventory.getStackInSlot(preferredSlot);
            if (stackToIncrease == null) {
                ItemStack newStack = getContainedItem(ist).copy();
                newStack.stackSize = Math.min(newStack.getMaxStackSize(), getContainedItem(ist).stackSize - 1);
                player.inventory.setInventorySlotContents(preferredSlot, newStack);
                return;
            }

            if (stackToIncrease.stackSize < stackToIncrease.getMaxStackSize()) {
                int quantityToDecrease = Math.min(stackToIncrease.getMaxStackSize() - stackToIncrease.stackSize, getContainedItem(ist).stackSize - 1);
                stackToIncrease.stackSize += quantityToDecrease;
                NBTHelper.setInteger("itemQuantity", ist, NBTHelper.getInteger("itemQuantity", ist) - quantityToDecrease);
            }
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack ist, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (world.getTileEntity(pos) instanceof IInventory) {
                IInventory inventory = (IInventory) world.getTileEntity(pos);

                if (inventory instanceof TileEntityChest && world.getBlockState(pos).getBlock() instanceof BlockChest)
                {
                    inventory = ((BlockChest) world.getBlockState(pos).getBlock()).getLockableContainer(world, pos);
                }

                //enabled == drinking mode, we're going to drain the inventory of items.
                if (this.isEnabled(ist)) {
                    this.drainInventory(ist, player, inventory);
                } else {
                    //disabled == placement mode, try and stuff the tear's contents into the inventory
                    this.attemptToEmptyIntoInventory(ist, player, inventory, 0);
                }
                return true;
            }
        }
        return false;
    }

    public ItemStack getContainedItem(ItemStack ist) {
        //something awful happened. We either lost data or this is an invalid tear by some other means. Either way, not great.
        if (NBTHelper.getString("itemID", ist).equals("") && (NBTHelper.getTagCompound("item", ist) == null || NBTHelper.getTagCompound("item", ist).hasNoTags()))
            return null;

        //backwards compatibility
        //TODO remove later
        if (!NBTHelper.getString("itemID", ist).equals("")) {
            return new ItemStack(Item.itemRegistry.getObject(new ResourceLocation(NBTHelper.getString("itemID", ist))), NBTHelper.getInteger("itemQuantity", ist));
        }

        ItemStack stackToReturn = ItemStack.loadItemStackFromNBT(NBTHelper.getTagCompound("item", ist));
        stackToReturn.stackSize = NBTHelper.getInteger("itemQuantity", ist);

        return stackToReturn;
    }

    protected boolean attemptToEmptyIntoInventory(ItemStack ist, EntityPlayer player, IInventory inventory, int limit) {
        ItemStack contents = this.getContainedItem(ist);
        contents.stackSize = 1;

        int quantity = NBTHelper.getInteger("itemQuantity", ist);
        int maxNumberToEmpty = player.isSneaking() ? quantity : Math.min(contents.getMaxStackSize(), quantity);

        quantity -= tryToAddToInventory(contents, inventory, limit, maxNumberToEmpty);

        NBTHelper.setInteger("itemQuantity", ist, quantity);
        if (quantity == 0) {
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
            return true;
        } else {
            player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));
            return false;
        }
    }

    protected void drainInventory(ItemStack ist, EntityPlayer player, IInventory inventory) {
        ItemStack contents = this.getContainedItem(ist);
        int quantity = NBTHelper.getInteger("itemQuantity", ist);

        int quantityDrained = tryToRemoveFromInventory(contents, inventory, Settings.VoidTear.itemLimit - quantity);

        if (!(quantityDrained > 0))
            return;

        player.worldObj.playSoundAtEntity(player, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.2F));

        NBTHelper.setInteger("itemQuantity", ist, quantity + quantityDrained);
    }

    public int tryToAddToInventory(ItemStack contents, IInventory inventory, int limit, int maxToAdd) {
        int numberAdded = 0;

        for (int slot = 0; slot < Math.min(inventory.getSizeInventory(), (limit > 0 ? limit : inventory.getSizeInventory())); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                ItemStack newContents = contents.copy();
                int stackAddition = Math.min(newContents.getMaxStackSize(), maxToAdd - numberAdded);
                newContents.stackSize = stackAddition;
                inventory.setInventorySlotContents(slot, newContents);
                numberAdded += stackAddition;
            } else if (StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents)) {
                if (inventory.getStackInSlot(slot).stackSize == inventory.getStackInSlot(slot).getMaxStackSize()) {
                    continue;
                }
                ItemStack slotStack = inventory.getStackInSlot(slot);
                int stackAddition = Math.min(slotStack.getMaxStackSize() - slotStack.stackSize, maxToAdd - numberAdded);
                slotStack.stackSize += stackAddition;
                numberAdded += stackAddition;
            }
            if (numberAdded >= maxToAdd)
                return numberAdded;

        }
        return numberAdded;
    }

    public int tryToRemoveFromInventory(ItemStack contents, IInventory inventory, int maxToRemove) {
        int numberRemoved = 0;

        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            if (inventory.getStackInSlot(slot) == null) {
                continue;
            }
            if (StackHelper.isItemAndNbtEqual(inventory.getStackInSlot(slot), contents)) {
                numberRemoved += Math.min(maxToRemove - numberRemoved, inventory.getStackInSlot(slot).stackSize);
                inventory.decrStackSize(slot, Math.min(maxToRemove, inventory.getStackInSlot(slot).stackSize));
            }

            if (numberRemoved >= maxToRemove)
                return numberRemoved;
        }
        return numberRemoved;
    }

}
