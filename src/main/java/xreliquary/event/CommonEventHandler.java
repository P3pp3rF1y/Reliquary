package xreliquary.event;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import xreliquary.init.ContentHandler;
import xreliquary.lib.Names;
import xreliquary.util.ObjectUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xreliquary.items.IVoidUpgradable;
import xreliquary.items.ItemVoidSatchel;
import xreliquary.items.ItemVoidTear;
import xreliquary.items.alkahestry.Alkahestry;
import xreliquary.lib.Reference;
import xreliquary.util.AlkahestRecipe;

public class CommonEventHandler {

	@SubscribeEvent
	public void onCraftingPotion(PlayerEvent.ItemCraftedEvent event) {
		for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
			if (event.craftMatrix.getStackInSlot(slot) == null) {
				continue;
			}
			if (event.craftMatrix.getStackInSlot(slot).getItem() == ContentHandler.getItem(Names.glowing_water))
				if (!event.player.inventory.addItemStackToInventory(new ItemStack(ContentHandler.getItem(Names.condensed_potion), 1, Reference.EMPTY_VIAL_META))) {
					event.player.entityDropItem(new ItemStack(ContentHandler.getItem(Names.condensed_potion), 1, Reference.EMPTY_VIAL_META), 0.1F);
				}
		}
	}

    @SubscribeEvent
    public void onCraftingAlkahest(PlayerEvent.ItemCraftedEvent event) {
        boolean isCharging = false;
        int tome = 9;
        AlkahestRecipe recipe = null;
        for (int count = 0; count < event.craftMatrix.getSizeInventory(); count++) {
            ItemStack stack = event.craftMatrix.getStackInSlot(count);
            if (stack != null) {
                if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(ContentHandler.getItem(Names.alkahest_tome)))) {
                    tome = count;
                } else if (ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getItemIdentifier(Items.redstone)) || ObjectUtils.getItemIdentifier(stack.getItem()).equals(ObjectUtils.getBlockIdentifier(Blocks.redstone_block))) {
                    isCharging = true;
                } else {
                    if (Alkahestry.getDictionaryKey(stack) == null) {
                        recipe = Alkahestry.getRegistry().get(ObjectUtils.getItemIdentifier(stack.getItem()));
                    } else {
                        recipe = Alkahestry.getDictionaryKey(stack);
                    }
                }
            }
        }
        if (tome != 9 && isCharging) {
            event.craftMatrix.setInventorySlotContents(tome, null);
        } else if (tome != 9 && !isCharging && recipe != null) {
            ItemStack temp = event.craftMatrix.getStackInSlot(tome);
            temp.setItemDamage(temp.getItemDamage() + recipe.cost);
            event.craftMatrix.setInventorySlotContents(tome, temp);
        }
    }

    @SubscribeEvent
    public void onSatchelUpgrade(PlayerEvent.ItemCraftedEvent event) {
            if (event.crafting == null)
                return;
            if (!(event.crafting.getItem() instanceof IVoidUpgradable))
                return;
            for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
                if (event.craftMatrix.getStackInSlot(slot) == null) {
                    continue;
                }
                if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_tear) && ObjectUtils.areItemsEqual(event.craftMatrix.getStackInSlot(slot).getItem(), Item.getItemFromBlock(ContentHandler.getBlock(Names.wraith_node)))) {
                    continue;
                }
                if (event.crafting.getItem() instanceof ItemVoidSatchel) {

                    ItemStack tear = event.craftMatrix.getStackInSlot(slot);

                    if (!(tear.getItem() instanceof ItemVoidTear))
                        continue;

                    NBTTagCompound tearData = tear.getTagCompound();

                    if (tearData == null)
                        continue;

                    String type = tearData.getString("itemID");
                    int meta = tearData.getShort("itemMeta");
                    int quantity = tearData.getShort("itemQuantity");
                    int leftover = 0;
                    int capacity = Reference.CAPACITY_UPGRADE_INCREMENT;
                    if (quantity > capacity) {
                        leftover = quantity - capacity;
                        quantity = capacity;
                    }
                    NBTTagCompound satchelData = new NBTTagCompound();
                    satchelData.setString("itemID", type);
                    satchelData.setShort("itemMeta", (short) meta);
                    satchelData.setShort("itemQuantity", (short) quantity);
                    satchelData.setShort("capacity", (short) capacity);
                    event.crafting.setTagCompound(satchelData);
                    if (leftover > 0) {
                        event.player.worldObj.playSoundAtEntity(event.player, "random.glass", 0.1F, 0.5F * ((event.player.worldObj.rand.nextFloat() - event.player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
                        while (leftover > 0) {
                            ItemStack spillage = new ItemStack((Item) Item.itemRegistry.getObject(type), 1, meta);
                            if (leftover > spillage.getMaxStackSize()) {
                                spillage.stackSize = spillage.getMaxStackSize();
                                leftover -= spillage.getMaxStackSize();
                            } else {
                                spillage.stackSize = leftover;
                                leftover = 0;
                            }
                            if (event.player.worldObj.isRemote) {
                                continue;
                            }
                            EntityItem item = new EntityItem(event.player.worldObj, event.player.posX, event.player.posY, event.player.posZ, spillage);
                            event.player.worldObj.spawnEntityInWorld(item);
                        }
                    }
                }
            }
            // handles upgrades for VoidSatchel
            for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
                if (event.craftMatrix.getStackInSlot(slot) == null) {
                    continue;
                }
                if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_satchel)) {
                    continue;
                }
                ItemStack oldSatchel = event.craftMatrix.getStackInSlot(slot);
                NBTTagCompound oldSatchelData = oldSatchel.getTagCompound();
                if (oldSatchelData == null) {
                    continue;
                }
                int type = oldSatchelData.getShort("itemID");
                int meta = oldSatchelData.getShort("itemMeta");
                int quantity = oldSatchelData.getShort("itemQuantity");
                int capacity = oldSatchelData.getShort("capacity");
                if (capacity >= 32000) {
                    for (int slot0 = 0; slot0 < event.craftMatrix.getSizeInventory(); slot++) {
                        if (event.craftMatrix.getStackInSlot(slot0) == null) {
                            continue;
                        }
                        if (event.craftMatrix.getStackInSlot(slot0).getItem() == ContentHandler.getItem(Names.void_tear_empty)) {
                            event.craftMatrix.getStackInSlot(slot0).stackSize++;
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
                event.crafting.setTagCompound(satchelData);
            }
    }

    @SubscribeEvent
    public void satchelHandler(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting == null)
            return;
        if (event.crafting.getItem() != ContentHandler.getItem(Names.void_satchel))
            return;
        // handles creation of VoidSatchel
        for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
            if (event.craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_tear)) {
                continue;
            }
            ItemStack tear = event.craftMatrix.getStackInSlot(slot);
            NBTTagCompound tearData = tear.getTagCompound();
            if (tearData == null) {
                continue;
            }
            String type = tearData.getString("itemID");
            int meta = tearData.getShort("itemMeta");
            int quantity = tearData.getShort("itemQuantity");
            int leftover = 0;
            int capacity = Reference.CAPACITY_UPGRADE_INCREMENT;
            if (quantity > capacity) {
                leftover = quantity - capacity;
                quantity = capacity;
            }
            NBTTagCompound satchelData = new NBTTagCompound();
            satchelData.setString("itemID", type);
            satchelData.setShort("itemMeta", (short) meta);
            satchelData.setShort("itemQuantity", (short) quantity);
            satchelData.setShort("capacity", (short) capacity);
            event.crafting.setTagCompound(satchelData);
            if (leftover > 0) {
                event.player.worldObj.playSoundAtEntity(event.player, "random.glass", 0.1F, 0.5F * ((event.player.worldObj.rand.nextFloat() - event.player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
                while (leftover > 0) {
                    ItemStack spillage = new ItemStack((Item) Item.itemRegistry.getObject(type), 1, meta);
                    if (leftover > spillage.getMaxStackSize()) {
                        spillage.stackSize = spillage.getMaxStackSize();
                        leftover -= spillage.getMaxStackSize();
                    } else {
                        spillage.stackSize = leftover;
                        leftover = 0;
                    }
                    if (event.player.worldObj.isRemote) {
                        continue;
                    }
                    EntityItem item = new EntityItem(event.player.worldObj, event.player.posX, event.player.posY, event.player.posZ, spillage);
                    event.player.worldObj.spawnEntityInWorld(item);
                }
            }
        }
        // handles upgrades for VoidSatchel
        for (int slot = 0; slot < event.craftMatrix.getSizeInventory(); slot++) {
            if (event.craftMatrix.getStackInSlot(slot) == null) {
                continue;
            }
            if (event.craftMatrix.getStackInSlot(slot).getItem() != ContentHandler.getItem(Names.void_satchel)) {
                continue;
            }
            ItemStack oldSatchel = event.craftMatrix.getStackInSlot(slot);
            NBTTagCompound oldSatchelData = oldSatchel.getTagCompound();
            if (oldSatchelData == null) {
                continue;
            }
            int type = oldSatchelData.getShort("itemID");
            int meta = oldSatchelData.getShort("itemMeta");
            int quantity = oldSatchelData.getShort("itemQuantity");
            int capacity = oldSatchelData.getShort("capacity");
            if (capacity >= 32000) {
                for (int slot0 = 0; slot0 < event.craftMatrix.getSizeInventory(); slot++) {
                    if (event.craftMatrix.getStackInSlot(slot0) == null) {
                        continue;
                    }
                    if (event.craftMatrix.getStackInSlot(slot0).getItem() == ContentHandler.getItem(Names.void_tear_empty)) {
                        event.craftMatrix.getStackInSlot(slot0).stackSize++;
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
            event.crafting.setTagCompound(satchelData);
        }
    }

}
